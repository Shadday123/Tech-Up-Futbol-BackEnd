package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InvitePlayerRequest {

    @NotBlank(message = "El nombre del equipo es necesario")
    private String teamName;

    @NotBlank(message = "El jugador es necesario")
    private String playerId;
}