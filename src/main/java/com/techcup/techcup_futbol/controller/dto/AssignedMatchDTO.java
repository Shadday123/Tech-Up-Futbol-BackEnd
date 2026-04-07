package com.techcup.techcup_futbol.controller.dto;

import java.time.LocalDateTime;

public record AssignedMatchDTO(
        String matchId,
        String localTeamName,
        String visitorTeamName,
        LocalDateTime dateTime,
        String field
) {}
