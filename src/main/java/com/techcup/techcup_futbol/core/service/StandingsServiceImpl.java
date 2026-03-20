package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.StandingsDTOs.*;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StandingsServiceImpl implements StandingsService {

    private static final Logger log = LoggerFactory.getLogger(StandingsServiceImpl.class);

    private final Map<String, Map<String, Standings>> tournamentStandings = new ConcurrentHashMap<>();
    private final Map<String, String> teamTournamentIndex = new ConcurrentHashMap<>();

    public void registerTeamInTournament(String tournamentId, Team team) {
        tournamentStandings.computeIfAbsent(tournamentId, k -> new LinkedHashMap<>());
        if (!tournamentStandings.get(tournamentId).containsKey(team.getId())) {
            Standings s = new Standings();
            s.setId(UUID.randomUUID().toString());
            s.setTeam(team);
            tournamentStandings.get(tournamentId).put(team.getId(), s);
            teamTournamentIndex.put(team.getId(), tournamentId);
        }
    }

    @Override
    public void updateFromMatch(Match match) {
        String tournamentId = teamTournamentIndex.get(match.getLocalTeam().getId());
        if (tournamentId == null) {
            log.warn("No se encontró torneo para el equipo '{}'. Standings no actualizados.",
                    match.getLocalTeam().getTeamName());
            return;
        }

        Map<String, Standings> table = tournamentStandings.get(tournamentId);
        if (table == null) return;

        updateTeamStandings(table, match.getLocalTeam(), match.getScoreLocal(), match.getScoreVisitor());
        updateTeamStandings(table, match.getVisitorTeam(), match.getScoreVisitor(), match.getScoreLocal());

        log.info("Tabla de posiciones actualizada para torneo ID: {}", tournamentId);
    }

    @Override
    public StandingsResponse findByTournamentId(String tournamentId) {
        Tournament tournament = DataStore.torneos.get(tournamentId);
        if (tournament == null) {
            throw new TournamentException("id",
                    String.format(TournamentException.TOURNAMENT_NOT_FOUND, tournamentId));
        }

        Map<String, Standings> table = tournamentStandings.getOrDefault(tournamentId, Map.of());

        List<Standings> sorted = table.values().stream()
                .sorted(Comparator
                        .comparingInt(Standings::getPoints).reversed()
                        .thenComparingInt(Standings::getGoalsDifference).reversed()
                        .thenComparingInt(Standings::getGoalsFor).reversed())
                .toList();

        List<TeamStandingDTO> dtos = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Standings s = sorted.get(i);
            dtos.add(new TeamStandingDTO(
                    i + 1,
                    s.getTeam().getId(),
                    s.getTeam().getTeamName(),
                    s.getTeam().getShieldUrl(),
                    s.getMatchesPlayed(), s.getMatchesWon(),
                    s.getMatchesDrawn(), s.getMatchesLost(),
                    s.getGoalsFor(), s.getGoalsAgainst(),
                    s.getGoalsDifference(), s.getPoints()
            ));
        }

        return new StandingsResponse(tournamentId, tournament.getName(), dtos);
    }

    private void updateTeamStandings(Map<String, Standings> table, Team team, int goalsFor, int goalsAgainst) {
        Standings s = table.get(team.getId());
        if (s == null) {
            s = new Standings();
            s.setId(UUID.randomUUID().toString());
            s.setTeam(team);
            table.put(team.getId(), s);
        }

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
    }
}
