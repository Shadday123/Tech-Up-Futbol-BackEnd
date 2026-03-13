package com.techcup.techcup_futbol.Controller.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.lang.Double;

public record CreateTournamentRequest(
        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Size(min = 5, max = 100)
        String name,

        @NotNull(message = "La fecha de inicio no puede ser nula")
        @Future(message = "La fecha de inicio debe ser en el futuro")
        LocalDateTime startDate,

        @NotNull(message = "La fecha de fin no puede ser nula")
        @Future(message = "La fecha de fin debe ser posterior a hoy")
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
