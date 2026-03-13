package com.techcup.techcup_futbol.Controller.dto;

import com.techcup.techcup_futbol.core.model.PositionEnum;

public record PlayerResponse(
        String id,
        String fullname,
        String email,
        PositionEnum position,
        Integer dorsalNumber,
        String photoUrl,
        boolean haveTeam,
        Integer age,
        String gender,
        boolean isCaptain,
        Integer semester,      // Se incluye si es estudiante
        String relationship    // Se incluye si es familiar
) {}
