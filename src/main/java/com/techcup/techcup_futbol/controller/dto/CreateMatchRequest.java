package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateMatchRequest(
        @NotBlank(message = "El equipo local es obligatorio")
        String localTeamId,

        @NotBlank(message = "El equipo visitante es obligatorio")
        String visitorTeamId,

        @NotNull(message = "La fecha y hora son obligatorias")
        LocalDateTime dateTime,

        String refereeId,

        @Min(value = 1, message = "El campo debe ser un número positivo")
        int field
) {}
