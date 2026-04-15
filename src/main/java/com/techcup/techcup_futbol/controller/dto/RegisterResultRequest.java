package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RegisterResultRequest(
        @NotNull(message = "Los goles locales son obligatorios")
        @Min(value = 0)
        Integer scoreLocal,

        @NotNull(message = "Los goles visitantes son obligatorios")
        @Min(value = 0)
        Integer scoreVisitor,

        List<MatchEventRequest> events
) {}
