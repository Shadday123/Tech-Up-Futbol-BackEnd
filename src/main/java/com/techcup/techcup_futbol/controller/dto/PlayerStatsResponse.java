package com.techcup.techcup_futbol.controller.dto;

public record PlayerStatsResponse(
        int matchesPlayed,
        int goals,
        int yellowCards,
        int redCards
) {}
