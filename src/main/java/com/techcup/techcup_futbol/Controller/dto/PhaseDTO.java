package com.techcup.techcup_futbol.Controller.dto;

import java.util.List;

public record PhaseDTO(
        String phase,
        List<BracketMatchDTO> matches
) {}
