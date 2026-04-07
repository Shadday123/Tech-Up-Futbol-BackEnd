package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateLineupRequest(
        @NotBlank(message = "El ID del partido es obligatorio")
        String matchId,

        @NotBlank(message = "El ID del equipo es obligatorio")
        String teamId,

        @NotBlank(message = "La formación es obligatoria")
        String formation,

        @NotNull(message = "Los titulares son obligatorios")
        @Size(min = 7, max = 7, message = "Debe seleccionar exactamente 7 titulares")
        List<String> starterIds,

        List<String> substituteIds,

        List<PlayerPositionDTO> fieldPositions
) {}
