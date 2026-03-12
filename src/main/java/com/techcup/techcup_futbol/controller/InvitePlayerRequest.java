package com.techcup.techcup_futbol.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InvitePlayerRequest {

    @NotBlank(message = "Team id is required")
    private String teamId;

    @NotBlank(message = "Player id is required")
    private String playerId;
}