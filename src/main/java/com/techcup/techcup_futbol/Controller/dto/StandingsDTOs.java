package com.techcup.techcup_futbol.Controller.dto;

import java.util.List;

public class StandingsDTOs {

    public record StandingsResponse(
            String tournamentId,
            String tournamentName,
            List<TeamStandingDTO> standings
    ) {}

    public record TeamStandingDTO(
            int position,
            String teamId,
            String teamName,
            String shieldUrl,
            int matchesPlayed,
            int matchesWon,
            int matchesDrawn,
            int matchesLost,
            int goalsFor,
            int goalsAgainst,
            int goalsDifference,
            int points
    ) {}
}
