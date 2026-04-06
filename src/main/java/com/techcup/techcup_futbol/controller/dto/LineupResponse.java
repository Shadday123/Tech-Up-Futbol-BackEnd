package com.techcup.techcup_futbol.Controller.dto;

import java.util.List;

public record LineupResponse(
        String id,
        String matchId,
        String teamId,
        String teamName,
        String formation,
        List<LineupPlayerDTO> starters,
        List<LineupPlayerDTO> substitutes,
        List<PlayerPositionDTO> fieldPositions
) {}
