package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateTeamRequest {

    @NotBlank(message = "El nombre del equipo es necesario")
    private String teamName;

    @NotBlank(message = "La imagen del equipo es necesaria")
    private String shieldUrl;

    @NotEmpty(message = "Los colores de los uniformes son necesarios")
    private List<String> uniformColors;

    @NotNull(message = "El capitán es necesario")
    private String captainId;

    @NotEmpty(message = "El equipo debe tener al menos un jugador al momento de su creación")
    private List<String> playerIds;
}
