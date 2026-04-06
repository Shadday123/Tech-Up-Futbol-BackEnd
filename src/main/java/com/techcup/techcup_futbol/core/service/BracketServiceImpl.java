package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.BracketException;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import com.techcup.techcup_futbol.persistence.repository.TournamentBracketsRepository;
import com.techcup.techcup_futbol.persistence.repository.TournamentRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BracketServiceImpl implements BracketService {

    private static final Logger log = LoggerFactory.getLogger(BracketServiceImpl.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentBracketsRepository tournamentBracketsRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    @Lazy
    private MatchService matchService;

    // ── GENERATE

    @Override
    @Transactional
    public List<TournamentBrackets> generate(String tournamentId, int teamsCount) {
        log.info("Generando llaves para torneo ID: {} con {} equipos",
                tournamentId, teamsCount);

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new BracketException("tournamentId",
                        String.format(BracketException.TOURNAMENT_NOT_FOUND, tournamentId)));

        List<TournamentBrackets> existing = tournamentBracketsRepository.findByTournamentId(tournamentId);
        if (!existing.isEmpty()) {
            boolean hasResults = existing.stream()
                    .flatMap(b -> b.getMatches().stream())
                    .anyMatch(m -> m.getStatus() == MatchStatus.FINISHED);
            if (hasResults) {
                throw new BracketException("bracket", BracketException.RESULTS_ALREADY_REGISTERED);
            }
            tournamentBracketsRepository.deleteAll(existing);
        }

        List<Team> teams = new ArrayList<>(teamRepository.findAll());
        int count = Math.min(teamsCount, teams.size());

        if (count < 2) {
            throw new BracketException("teams",
                    String.format(BracketException.NOT_ENOUGH_TEAMS, count));
        }
        if (!isPowerOfTwo(count)) {
            throw new BracketException("teams",
                    String.format(BracketException.TEAMS_NOT_POWER_OF_TWO, count));
        }

        Collections.shuffle(teams.subList(0, count), new java.security.SecureRandom());
        List<Team> selected = new ArrayList<>(teams.subList(0, count));

        PhaseEnum phase = resolveInitialPhase(count);
        List<Match> roundMatches = new ArrayList<>();

        for (int i = 0; i < selected.size() - 1; i += 2) {
            Match m = buildMatch(selected.get(i), selected.get(i + 1));
            matchRepository.save(m);
            roundMatches.add(m);
            matchService.registerMatch(m);
        }

        TournamentBrackets bracket = new TournamentBrackets();
        bracket.setId(IdGenerator.generateId());
        bracket.setTournament(tournament);
        bracket.setPhase(phase);
        bracket.setMatches(roundMatches);
        tournamentBracketsRepository.save(bracket);

        log.info("Llaves generadas: {} partidos en fase {}", roundMatches.size(), phase);
        return List.of(bracket);
    }

    // ── FIND

    @Override
    public List<TournamentBrackets> findByTournamentId(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new BracketException("tournamentId",
                        String.format(BracketException.TOURNAMENT_NOT_FOUND, tournamentId)));

        List<TournamentBrackets> phases = tournamentBracketsRepository.findByTournamentId(tournamentId);
        if (phases.isEmpty()) {
            throw new BracketException("bracket",
                    String.format(BracketException.BRACKET_NOT_FOUND, tournament.getName()));
        }
        return phases;
    }

    // ── ADVANCE WINNER

    @Override
    @Transactional
    public List<TournamentBrackets> advanceWinner(String tournamentId, String matchId) {
        log.info("Avanzando ganador del partido ID: {}", matchId);

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new BracketException("tournamentId",
                        String.format(BracketException.TOURNAMENT_NOT_FOUND, tournamentId)));

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BracketException("matchId",
                        String.format(BracketException.MATCH_NOT_FOUND, matchId)));

        if (!matchService.isResultRegistered(matchId)) {
            throw new BracketException("matchId", BracketException.RESULT_NOT_REGISTERED);
        }

        if (match.getScoreLocal() == match.getScoreVisitor()) {
            throw new BracketException("match", BracketException.DRAW_NO_WINNER);
        }

        Team winner = match.getScoreLocal() > match.getScoreVisitor()
                ? match.getLocalTeam()
                : match.getVisitorTeam();

        match.setWinner(winner);
        match.setStatus(MatchStatus.FINISHED);
        matchRepository.save(match);

        List<TournamentBrackets> phases = tournamentBracketsRepository.findByTournamentId(tournamentId);
        TournamentBrackets currentPhase = phases.get(phases.size() - 1);

        boolean allFinished = currentPhase.getMatches().stream()
                .allMatch(m -> m.getStatus() == MatchStatus.FINISHED && m.getWinner() != null);

        if (allFinished && currentPhase.getPhase() != PhaseEnum.FINAL) {
            List<Team> winners = currentPhase.getMatches().stream()
                    .map(Match::getWinner)
                    .filter(Objects::nonNull)
                    .toList();

            PhaseEnum nextPhase = nextPhase(currentPhase.getPhase());
            List<Match> nextMatches = new ArrayList<>();
            for (int i = 0; i < winners.size() - 1; i += 2) {
                Match nm = buildMatch(winners.get(i), winners.get(i + 1));
                matchRepository.save(nm);
                nextMatches.add(nm);
                matchService.registerMatch(nm);
            }

            TournamentBrackets nextBracket = new TournamentBrackets();
            nextBracket.setId(IdGenerator.generateId());
            nextBracket.setTournament(tournament);
            nextBracket.setPhase(nextPhase);
            nextBracket.setMatches(nextMatches);
            tournamentBracketsRepository.save(nextBracket);

            phases = tournamentBracketsRepository.findByTournamentId(tournamentId);
            log.info("Fase {} generada con {} partidos", nextPhase, nextMatches.size());
        }

        return phases;
    }

    // ── HELPERS

    private Match buildMatch(Team local, Team visitor) {
        Match m = new Match();
        m.setId(IdGenerator.generateId());
        m.setLocalTeam(local);
        m.setVisitorTeam(visitor);
        m.setStatus(MatchStatus.SCHEDULED);
        return m;
    }

    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    private PhaseEnum resolveInitialPhase(int count) {
        return switch (count) {
            case 2  -> PhaseEnum.FINAL;
            case 4  -> PhaseEnum.SEMI_FINALS;
            case 8  -> PhaseEnum.QUARTER_FINALS;
            default -> PhaseEnum.INITIAL_ROUND;
        };
    }

    private PhaseEnum nextPhase(PhaseEnum current) {
        return switch (current) {
            case INITIAL_ROUND  -> PhaseEnum.QUARTER_FINALS;
            case QUARTER_FINALS -> PhaseEnum.SEMI_FINALS;
            case SEMI_FINALS    -> PhaseEnum.FINAL;
            default             -> PhaseEnum.FINAL;
        };
    }
}
