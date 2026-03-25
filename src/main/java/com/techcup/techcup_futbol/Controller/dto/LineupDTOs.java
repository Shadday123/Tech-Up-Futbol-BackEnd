package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class LineupDTOs {

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

    public record PlayerPositionDTO(
            String playerId,
            double x,
            double y
    ) {}

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

    public record LineupPlayerDTO(
            String id,
            String fullname,
            String position,
            int dorsalNumber,
            String photoUrl
    ) {}
}
