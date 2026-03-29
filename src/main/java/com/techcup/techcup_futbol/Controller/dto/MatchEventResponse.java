package com.techcup.techcup_futbol.Controller.dto;

public record MatchEventResponse(
        String id,
        String type,
        int minute,
        String playerId,
        String playerName
) {}
