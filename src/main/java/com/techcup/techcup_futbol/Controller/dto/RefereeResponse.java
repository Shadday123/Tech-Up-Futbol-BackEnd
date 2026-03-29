package com.techcup.techcup_futbol.Controller.dto;

import java.util.List;

public record RefereeResponse(
        String id,
        String fullname,
        String email,
        List<AssignedMatchDTO> assignedMatches
) {}
