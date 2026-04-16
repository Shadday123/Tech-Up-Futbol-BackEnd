package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.*;

public record RefereeRegistrationRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String fullname,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
                message = "La contraseña debe tener al menos una mayúscula y un número")
        String password,

        @NotBlank(message = "El número de licencia es obligatorio")
        String license,

        @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
        int experience
) {}
