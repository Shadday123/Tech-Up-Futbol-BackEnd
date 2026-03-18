package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.BracketDTOs.*;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.exception.BracketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BracketServiceImpl implements BracketService {

    private static final Logger log = LoggerFactory.getLogger(BracketServiceImpl.class);

    private final Map<String, List<TournamentBrackets>> tournamentBrackets = new HashMap<>();
    private final Map<String, Match> bracketMatches = new LinkedHashMap<>();
    private final Map<String, MatchStatus> bracketMatchStatus = new HashMap<>();
    private final Map<String, Team> matchWinners = new HashMap<>();

    @Autowired
    private MatchServiceImpl matchService;

    @Override
    public BracketResponse generate(String tournamentId, GenerateBracketRequest request) {
        log.info("Generando llaves para torneo ID: {} con {} equipos", tournamentId, request.teamsCount());

        Tournament tournament = DataStore.torneos.get(tournamentId);
        if (tournament == null) {
            throw new BracketException("tournamentId",
                    String.format(BracketException.TOURNAMENT_NOT_FOUND, tournamentId));
        }
        if (tournamentBrackets.containsKey(tournamentId)) {
            boolean hasResults = bracketMatches.values().stream()
                    .anyMatch(m -> bracketMatchStatus.get(m.getId()) == MatchStatus.FINISHED);
            if (hasResults) {
                throw new BracketException("bracket", BracketException.RESULTS_ALREADY_REGISTERED);
            }
            tournamentBrackets.remove(tournamentId);
        }

        List<Team> teams = new ArrayList<>(DataStore.equipos.values());
        int count = Math.min(request.teamsCount(), teams.size());

        if (count < 2) {
            throw new BracketException("teams",
                    String.format(BracketException.NOT_ENOUGH_TEAMS, count));
        }
        if (!isPowerOfTwo(count)) {
            throw new BracketException("teams",
                    String.format(BracketException.TEAMS_NOT_POWER_OF_TWO, count));
        }

        Collections.shuffle(teams.subList(0, count), new java.security.SecureRandom());
        List<Team> selected = teams.subList(0, count);

        List<TournamentBrackets> phases = new ArrayList<>();
        PhaseEnum phase = resolveInitialPhase(count);

        List<Match> roundMatches = new ArrayList<>();
        for (int i = 0; i < selected.size() - 1; i += 2) {
            Match m = buildMatch(selected.get(i), selected.get(i + 1));
            roundMatches.add(m);
            bracketMatches.put(m.getId(), m);
            bracketMatchStatus.put(m.getId(), MatchStatus.SCHEDULED);
            matchService.getMatches().put(m.getId(), m);
        }

        TournamentBrackets bracket = new TournamentBrackets();
        bracket.setId(UUID.randomUUID().toString());
        bracket.setTournament(tournament);
        bracket.setPhase(phase);
        bracket.setMatches(roundMatches);
        phases.add(bracket);

        tournamentBrackets.put(tournamentId, phases);
        log.info("Llaves generadas: {} partidos en fase {}", roundMatches.size(), phase);
        return toResponse(tournamentId, tournament, phases);
    }

    @Override
    public BracketResponse findByTournamentId(String tournamentId) {
        Tournament tournament = DataStore.torneos.get(tournamentId);
        if (tournament == null) {
            throw new BracketException("tournamentId",
                    String.format(BracketException.TOURNAMENT_NOT_FOUND, tournamentId));
        }
        List<TournamentBrackets> phases = tournamentBrackets.get(tournamentId);
        if (phases == null || phases.isEmpty()) {
            throw new BracketException("bracket",
                    String.format(BracketException.BRACKET_NOT_FOUND, tournament.getName()));
        }
        return toResponse(tournamentId, tournament, phases);
    }

    @Override
    public BracketResponse advanceWinner(String tournamentId, String matchId) {
        log.info("Avanzando ganador del partido ID: {}", matchId);

        Tournament tournament = DataStore.torneos.get(tournamentId);
        if (tournament == null) {
            throw new BracketException("tournamentId",
                    String.format(BracketException.TOURNAMENT_NOT_FOUND, tournamentId));
        }

        Match match = bracketMatches.get(matchId);
        if (match == null) {
            throw new BracketException("matchId",
                    String.format(BracketException.MATCH_NOT_FOUND, matchId));
        }

        Team winner = match.getScoreLocal() >= match.getScoreVisitor()
                ? match.getLocalTeam() : match.getVisitorTeam();
        matchWinners.put(matchId, winner);
        bracketMatchStatus.put(matchId, MatchStatus.FINISHED);

        List<TournamentBrackets> phases = tournamentBrackets.get(tournamentId);
        TournamentBrackets currentPhase = phases.get(phases.size() - 1);

        boolean allFinished = currentPhase.getMatches().stream()
                .allMatch(m -> bracketMatchStatus.get(m.getId()) == MatchStatus.FINISHED);

        if (allFinished && currentPhase.getPhase() != PhaseEnum.FINAL) {
            List<Team> winners = currentPhase.getMatches().stream()
                    .map(m -> matchWinners.get(m.getId()))
                    .filter(Objects::nonNull)
                    .toList();

            PhaseEnum nextPhase = nextPhase(currentPhase.getPhase());
            List<Match> nextMatches = new ArrayList<>();
            for (int i = 0; i < winners.size() - 1; i += 2) {
                Match nm = buildMatch(winners.get(i), winners.get(i + 1));
                nextMatches.add(nm);
                bracketMatches.put(nm.getId(), nm);
                bracketMatchStatus.put(nm.getId(), MatchStatus.SCHEDULED);
                matchService.getMatches().put(nm.getId(), nm);
            }

            TournamentBrackets nextBracket = new TournamentBrackets();
            nextBracket.setId(UUID.randomUUID().toString());
            nextBracket.setTournament(tournament);
            nextBracket.setPhase(nextPhase);
            nextBracket.setMatches(nextMatches);
            phases.add(nextBracket);
            log.info("Fase {} generada con {} partidos", nextPhase, nextMatches.size());
        }

        return toResponse(tournamentId, tournament, phases);
    }

    private Match buildMatch(Team local, Team visitor) {
        Match m = new Match();
        m.setId(UUID.randomUUID().toString());
        m.setLocalTeam(local);
        m.setVisitorTeam(visitor);
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

    private BracketResponse toResponse(String tournamentId, Tournament tournament,
                                        List<TournamentBrackets> phases) {
        List<PhaseDTO> phaseDTOs = phases.stream().map(b -> {
            List<BracketMatchDTO> matchDTOs = b.getMatches() == null ? List.of()
                    : b.getMatches().stream().map(m -> {
                        Team w = matchWinners.get(m.getId());
                        MatchStatus st = bracketMatchStatus.getOrDefault(m.getId(), MatchStatus.SCHEDULED);
                        return new BracketMatchDTO(
                                m.getId(),
                                m.getLocalTeam().getId(), m.getLocalTeam().getTeamName(),
                                m.getVisitorTeam().getId(), m.getVisitorTeam().getTeamName(),
                                st == MatchStatus.FINISHED ? m.getScoreLocal() : null,
                                st == MatchStatus.FINISHED ? m.getScoreVisitor() : null,
                                w != null ? w.getId() : null,
                                w != null ? w.getTeamName() : null,
                                st.name()
                        );
                    }).toList();
            return new PhaseDTO(b.getPhase().name(), matchDTOs);
        }).toList();

        return new BracketResponse(tournamentId, tournament.getName(), phaseDTOs);
    }
}
