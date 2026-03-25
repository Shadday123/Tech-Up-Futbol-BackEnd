package com.techcup.techcup_futbol.Controller.dto;

import java.util.List;

public class BracketDTOs {

    public record GenerateBracketRequest(
            int teamsCount
    ) {}

    public record BracketResponse(
            String tournamentId,
            String tournamentName,
            List<PhaseDTO> phases
    ) {}

    public record PhaseDTO(
            String phase,
            List<BracketMatchDTO> matches
    ) {}

    public record BracketMatchDTO(
            String matchId,
            String localTeamId,
            String localTeamName,
            String visitorTeamId,
            String visitorTeamName,
            Integer scoreLocal,
            Integer scoreVisitor,
            String winnerId,
            String winnerName,
            String status
    ) {}
}
