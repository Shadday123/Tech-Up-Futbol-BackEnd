package com.techcup.techcup_futbol.controller.dto;

import java.util.List;

public record PhaseDTO(
        String phase,
        List<BracketMatchDTO> matches
) {}
