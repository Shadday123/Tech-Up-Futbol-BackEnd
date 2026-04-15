package com.techcup.techcup_futbol.controller.dto;

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
