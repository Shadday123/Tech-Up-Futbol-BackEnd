package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.StandingsResponse;
import com.techcup.techcup_futbol.Controller.mapper.StandingsMapper;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.repository.StandingsRepository;
import com.techcup.techcup_futbol.repository.TournamentRepository;
import com.techcup.techcup_futbol.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StandingsServiceImpl implements StandingsService {

    private static final Logger log = LoggerFactory.getLogger(StandingsServiceImpl.class);

    @Autowired
    private StandingsRepository standingsRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    // Índice en memoria para saber a qué torneo pertenece cada equipo
    private final Map<String, String> teamTournamentIndex = new ConcurrentHashMap<>();

    public void registerTeamInTournament(String tournamentId, Team team) {
        teamTournamentIndex.put(team.getId(), tournamentId);

        boolean exists = standingsRepository
                .findByTournamentIdAndTeamId(tournamentId, team.getId())
                .isPresent();

        if (!exists) {
            Standings s = new Standings();
            s.setId(IdGenerator.generateId());
            s.setTournamentId(tournamentId);
            s.setTeam(team);
            standingsRepository.save(s);
        }
    }

    @Override
    @Transactional
    public void updateFromMatch(Match match) {
        String tournamentId = teamTournamentIndex.get(match.getLocalTeam().getId());
        if (tournamentId == null) {
            log.warn("No se encontró torneo para el equipo '{}'. Standings no actualizados.",
                    match.getLocalTeam().getTeamName());
            return;
        }

        updateTeamStandings(tournamentId, match.getLocalTeam(),
                match.getScoreLocal(), match.getScoreVisitor());
        updateTeamStandings(tournamentId, match.getVisitorTeam(),
                match.getScoreVisitor(), match.getScoreLocal());

        log.info("Tabla de posiciones actualizada para torneo ID: {}", tournamentId);
    }

    @Override
    public StandingsResponse findByTournamentId(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentException("id",
                        String.format(TournamentException.TOURNAMENT_NOT_FOUND, tournamentId)));

        List<Standings> sorted = standingsRepository.findByTournamentId(tournamentId)
                .stream()
                .sorted(Comparator
                        .comparingInt(Standings::getPoints).reversed()
                        .thenComparingInt(Standings::getGoalsDifference).reversed()
                        .thenComparingInt(Standings::getGoalsFor).reversed())
                .toList();

        return StandingsMapper.toResponse(tournamentId, tournament.getName(), sorted);
    }

    private void updateTeamStandings(String tournamentId, Team team, int goalsFor, int goalsAgainst) {
        Standings s = standingsRepository
                .findByTournamentIdAndTeamId(tournamentId, team.getId())
                .orElseGet(() -> {
                    Standings ns = new Standings();
                    ns.setId(IdGenerator.generateId());
                    ns.setTournamentId(tournamentId);
                    ns.setTeam(team);
                    return ns;
                });

        s.setMatchesPlayed(s.getMatchesPlayed() + 1);
        s.setGoalsFor(s.getGoalsFor() + goalsFor);
        s.setGoalsAgainst(s.getGoalsAgainst() + goalsAgainst);
        s.setGoalsDifference(s.getGoalsFor() - s.getGoalsAgainst());

        if (goalsFor > goalsAgainst) {
            s.setMatchesWon(s.getMatchesWon() + 1);
            s.setPoints(s.getPoints() + 3);
        } else if (goalsFor == goalsAgainst) {
            s.setMatchesDrawn(s.getMatchesDrawn() + 1);
            s.setPoints(s.getPoints() + 1);
        } else {
            s.setMatchesLost(s.getMatchesLost() + 1);
        }

        standingsRepository.save(s);
    }
}
