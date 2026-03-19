package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateTournamentRequest(

        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Size(min = 5, max = 100, message = "El nombre debe tener entre 5 y 100 caracteres")
        String name,

        @NotNull(message = "La fecha de inicio no puede ser nula")
        LocalDateTime startDate,

        @NotNull(message = "La fecha de fin no puede ser nula")
        LocalDateTime endDate,

        @NotNull(message = "El costo de inscripción es obligatorio")
        @PositiveOrZero(message = "El costo no puede ser negativo")
        Double registrationFee,

        @Min(value = 4, message = "El torneo debe tener al menos 4 equipos")
        @Max(value = 32, message = "El máximo de equipos permitido es 32")
        int maxTeams,

        @NotBlank(message = "Las reglas del torneo son obligatorias")
        String rules
) {}