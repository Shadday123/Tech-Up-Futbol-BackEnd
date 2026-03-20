package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class MatchDTOs {

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

    public record RegisterResultRequest(
            @NotNull(message = "Los goles locales son obligatorios")
            @Min(value = 0)
            Integer scoreLocal,

            @NotNull(message = "Los goles visitantes son obligatorios")
            @Min(value = 0)
            Integer scoreVisitor,

            List<MatchEventRequest> events
    ) {}

    public record MatchEventRequest(
            @NotBlank(message = "El tipo de evento es obligatorio")
            String type,

            @Min(value = 1)
            int minute,

            @NotBlank(message = "El ID del jugador es obligatorio")
            String playerId
    ) {}

    public record MatchResponse(
            String id,
            String localTeamId,
            String localTeamName,
            String visitorTeamId,
            String visitorTeamName,
            LocalDateTime dateTime,
            int scoreLocal,
            int scoreVisitor,
            int yellowCards,
            int redCards,
            int field,
            String status,
            List<MatchEventResponse> events
    ) {}

    public record MatchEventResponse(
            String id,
            String type,
            int minute,
            String playerId,
            String playerName
    ) {}
}
