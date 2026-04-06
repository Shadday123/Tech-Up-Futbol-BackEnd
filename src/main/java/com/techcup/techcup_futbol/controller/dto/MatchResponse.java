package com.techcup.techcup_futbol.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MatchResponse(
        String id,
        String localTeamId,
        String localTeamName,
        String visitorTeamId,
        String visitorTeamName,
        LocalDateTime dateTime,
        int scoreLocal,
        int scoreVisitor,
        int yellowCards,
        int redCards,
        int field,
        String status,
        List<MatchEventResponse> events
) {}
