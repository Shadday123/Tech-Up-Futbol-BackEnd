package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateRefereeRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String fullname,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe ser válido")
        String email
) {}
