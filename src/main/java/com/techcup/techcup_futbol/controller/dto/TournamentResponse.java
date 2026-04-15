package com.techcup.techcup_futbol.controller.dto;

import java.time.LocalDateTime;

public record TournamentResponse(
        String id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Double registrationFee,
        int maxTeams,
        String rules,
        String currentState //  Enum
) {}
