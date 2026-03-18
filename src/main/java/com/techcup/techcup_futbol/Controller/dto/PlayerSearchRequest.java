package com.techcup.techcup_futbol.Controller.dto;

import com.techcup.techcup_futbol.core.model.PositionEnum;

public record PlayerSearchRequest(
        PositionEnum position,
        Integer semester,
        Integer minAge,
        Integer maxAge,
        String gender,
        String name,
        Integer numberID
) {}
