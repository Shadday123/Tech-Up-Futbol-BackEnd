package com.techcup.techcup_futbol.controller.dto;

public record BracketMatchDTO(
        String matchId,
        String localTeamId,
        String localTeamName,
        String visitorTeamId,
        String visitorTeamName,
        Integer scoreLocal,
        Integer scoreVisitor,
        String winnerId,
        String winnerName,
        String status
) {}
