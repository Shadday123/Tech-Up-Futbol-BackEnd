package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateTournamentConfigRequest(
        @NotBlank(message = "El reglamento es obligatorio")
        String rules,

        @NotNull(message = "El cierre de inscripciones es obligatorio")
        LocalDateTime registrationDeadline,

        @NotNull(message = "Las fechas importantes son obligatorias")
        List<ImportantDateDTO> importantDates,

        @NotNull(message = "Los horarios de partidos son obligatorios")
        List<MatchScheduleDTO> matchSchedules,

        @NotNull(message = "Las canchas son obligatorias")
        List<FieldDTO> fields,

        @NotBlank(message = "Las sanciones son obligatorias")
        String sanctions
) {}
