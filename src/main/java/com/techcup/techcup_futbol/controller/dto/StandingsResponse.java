package com.techcup.techcup_futbol.Controller.dto;

import java.util.List;

public record StandingsResponse(
        String tournamentId,
        String tournamentName,
        List<TeamStandingDTO> standings
) {}
