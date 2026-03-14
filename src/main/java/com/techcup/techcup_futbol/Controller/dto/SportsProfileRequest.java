package com.techcup.techcup_futbol.Controller.dto;

import com.techcup.techcup_futbol.core.model.PositionEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SportsProfileRequest(
        @NotNull(message = "Position is required")
        PositionEnum position,

        @NotNull(message = "Dorsal number is required")
        @Min(value = 1, message = "Dorsal must be at least 1")
        @Max(value = 99, message = "Dorsal cannot exceed 99")
        Integer dorsalNumber
) {}
