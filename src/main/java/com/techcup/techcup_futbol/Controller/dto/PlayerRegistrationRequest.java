package com.techcup.techcup_futbol.Controller.dto;


import jakarta.validation.constraints.*;

public record PlayerRegistrationRequest(
        @NotBlank(message = "Full name is required")
        String fullname,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "ID number is required")
        Integer numberID,

        @NotNull(message = "Age is required")
        @Min(value = 16, message = "Minimum age is 16")
        Integer age,

        @NotBlank(message = "Gender is required")
        String gender,

        // Atributos opcionales según el tipo de jugador
        Integer semester,      // Para StudentPlayer
        String relationship    // Para RelativePlayer
) {}
