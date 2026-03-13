package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeamRequest {

    @NotBlank(message = "El nombre del equipo es necesario")
    private String teamName;

    @NotBlank(message = "La imagen del equipo es necesario")
    private String shieldUrl;

    @NotBlank(message = "Los colores de los uniformes son necesarios")
    private String uniformColors;

    @NotNull(message = "El capitan es necesario")
    private String captainId;
}