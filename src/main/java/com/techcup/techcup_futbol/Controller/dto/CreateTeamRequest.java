package com.techcup.techcup_futbol.Controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotBlank(message = "El nombre del equipo es obligatorio")
        @Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
        String name,

        @NotBlank(message = "Debes proporcionar un escudo")
        String logoUrl,

        @NotBlank(message = "Define los colores del equipo")
        String colorPrimary,

        String colorSecondary,

        @NotBlank(message = "El equipo debe tener un capitán asignado")
        String captainId
) {}
