package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignRefereeRequest(
        @NotBlank(message = "El ID del árbitro es obligatorio")
        String refereeId
) {}
