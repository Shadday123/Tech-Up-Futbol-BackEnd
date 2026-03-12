package com.techcup.techcup_futbol.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    private String teamName;

    private String shieldUrl;

    @NotBlank(message = "Uniform colors are required")
    private String uniformColors;

    @NotNull(message = "Captain id is required")
    private String captainId;
}