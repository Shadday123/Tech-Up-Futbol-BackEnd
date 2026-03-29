package com.techcup.techcup_futbol.Controller.mapper;

import com.techcup.techcup_futbol.Controller.dto.StandingsResponse;
import com.techcup.techcup_futbol.Controller.dto.TeamStandingDTO;
import com.techcup.techcup_futbol.core.model.Standings;

import java.util.ArrayList;
import java.util.List;

public class StandingsMapper {

    private StandingsMapper() {}

    public static StandingsResponse toResponse(String tournamentId, String tournamentName,
                                                List<Standings> sorted) {
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
        return new StandingsResponse(tournamentId, tournamentName, dtos);
    }
}
