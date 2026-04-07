package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MatchEventRequest(
        @NotBlank(message = "El tipo de evento es obligatorio")
        String type,

        @Min(value = 1)
        int minute,

        @NotBlank(message = "El ID del jugador es obligatorio")
        String playerId
) {}
