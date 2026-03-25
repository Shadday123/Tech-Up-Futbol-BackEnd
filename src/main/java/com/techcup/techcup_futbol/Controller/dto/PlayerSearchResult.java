package com.techcup.techcup_futbol.Controller.dto;

import com.techcup.techcup_futbol.core.model.PositionEnum;

public record PlayerSearchResult(
        String id,
        String fullname,
        PositionEnum position,
        Integer dorsalNumber,
        String photoUrl,
        String playerType,
        Integer semester,
        Integer age,
        String gender,
        boolean available
) {}
