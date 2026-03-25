package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class RefereeDTOs {

    public record CreateRefereeRequest(
            @NotBlank(message = "El nombre es obligatorio")
            String fullname,

            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo debe ser válido")
            String email
    ) {}

    public record AssignRefereeRequest(
            @NotBlank(message = "El ID del árbitro es obligatorio")
            String refereeId
    ) {}

    public record RefereeResponse(
            String id,
            String fullname,
            String email,
            List<AssignedMatchDTO> assignedMatches
    ) {}

    public record AssignedMatchDTO(
            String matchId,
            String localTeamName,
            String visitorTeamName,
            LocalDateTime dateTime,
            String field
    ) {}
}
