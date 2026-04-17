package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateTeamRequest {

    @NotBlank(message = "El nombre del equipo es necesario")
    private String teamName;

    private String shieldUrl;

    @NotEmpty(message = "Los colores de los uniformes son necesarios")
    private List<String> uniformColors;

    @NotNull(message = "El capitán es necesario")
    private String captainId;

    private List<String> playerIds = new ArrayList<>();
}
