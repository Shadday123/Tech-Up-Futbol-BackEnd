package com.techcup.techcup_futbol.controller.dto;

import java.util.List;

public record BracketResponse(
        String tournamentId,
        String tournamentName,
        List<PhaseDTO> phases
) {}
