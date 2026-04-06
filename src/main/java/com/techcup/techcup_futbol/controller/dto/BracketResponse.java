package com.techcup.techcup_futbol.Controller.dto;

import java.util.List;

public record BracketResponse(
        String tournamentId,
        String tournamentName,
        List<PhaseDTO> phases
) {}
