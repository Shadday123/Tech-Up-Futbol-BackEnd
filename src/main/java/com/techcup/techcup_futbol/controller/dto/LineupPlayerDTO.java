package com.techcup.techcup_futbol.controller.dto;

public record LineupPlayerDTO(
        String id,
        String fullname,
        String position,
        int dorsalNumber,
        String photoUrl
) {}
