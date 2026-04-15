package com.techcup.techcup_futbol.controller.mapper;

import com.techcup.techcup_futbol.controller.dto.BracketMatchDTO;
import com.techcup.techcup_futbol.controller.dto.BracketResponse;
import com.techcup.techcup_futbol.controller.dto.PhaseDTO;
import com.techcup.techcup_futbol.core.model.MatchStatus;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentBrackets;
import com.techcup.techcup_futbol.persistence.entity.TournamentBracketsEntity;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.core.model.Match;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BracketMapper {

    private BracketMapper() {}

    public static BracketResponse toResponse(String tournamentId, Tournament tournament,
                                             List<TournamentBracketsEntity> phases) {
        List<PhaseDTO> phaseDTOs = Optional.ofNullable(phases)
                .orElse(List.of())
                .stream()
                .map(BracketMapper::toPhaseDTO)
                .collect(Collectors.toList());

        return new BracketResponse(tournamentId, tournament.getName(), phaseDTOs);
    }

    public static BracketResponse toResponseFromModels(String tournamentId, Tournament tournament,
                                                       List<TournamentBrackets> phases) {
        List<PhaseDTO> phaseDTOs = Optional.ofNullable(phases)
                .orElse(List.of())
                .stream()
                .map(BracketMapper::toPhaseDTOFromModel)
                .collect(Collectors.toList());

        return new BracketResponse(tournamentId, tournament.getName(), phaseDTOs);
    }

    private static PhaseDTO toPhaseDTO(TournamentBracketsEntity bracket) {
        List<BracketMatchDTO> matchDTOs = Optional.ofNullable(bracket.getMatches())
                .orElse(List.of())
                .stream()
                .map(BracketMapper::toMatchDTO)
                .collect(Collectors.toList());

        return new PhaseDTO(bracket.getPhase().name(), matchDTOs);
    }

    private static PhaseDTO toPhaseDTOFromModel(TournamentBrackets bracket) {
        List<BracketMatchDTO> matchDTOs = Optional.ofNullable(bracket.getMatches())
                .orElse(List.of())
                .stream()
                .map(BracketMapper::toMatchDTOFromModel)
                .collect(Collectors.toList());

        return new PhaseDTO(bracket.getPhase().name(), matchDTOs);
    }

    private static BracketMatchDTO toMatchDTO(MatchEntity match) {
        TeamEntity localTeamEntity = match.getLocalTeam();
        TeamEntity visitorTeamEntity = match.getVisitorTeam();
        TeamEntity winnerEntity = match.getWinner();
        MatchStatus status = match.getStatus() != null ? match.getStatus() : MatchStatus.SCHEDULED;

        Team localTeam = localTeamEntity != null ? toModelTeam(localTeamEntity) : null;
        Team visitorTeam = visitorTeamEntity != null ? toModelTeam(visitorTeamEntity) : null;
        Team winner = winnerEntity != null ? toModelTeam(winnerEntity) : null;

        return new BracketMatchDTO(
                match.getId(),
                localTeam != null ? localTeam.getId() : null,
                localTeam != null ? localTeam.getTeamName() : null,
                visitorTeam != null ? visitorTeam.getId() : null,
                visitorTeam != null ? visitorTeam.getTeamName() : null,
                status == MatchStatus.FINISHED ? match.getScoreLocal() : null,
                status == MatchStatus.FINISHED ? match.getScoreVisitor() : null,
                winner != null ? winner.getId() : null,
                winner != null ? winner.getTeamName() : null,
                status.name()
        );
    }

    private static BracketMatchDTO toMatchDTOFromModel(Match match) {
        Team localTeam = match.getLocalTeam();
        Team visitorTeam = match.getVisitorTeam();
        Team winner = match.getWinner();
        MatchStatus status = match.getStatus() != null ? match.getStatus() : MatchStatus.SCHEDULED;

        return new BracketMatchDTO(
                match.getId(),
                localTeam != null ? localTeam.getId() : null,
                localTeam != null ? localTeam.getTeamName() : null,
                visitorTeam != null ? visitorTeam.getId() : null,
                visitorTeam != null ? visitorTeam.getTeamName() : null,
                status == MatchStatus.FINISHED ? match.getScoreLocal() : null,
                status == MatchStatus.FINISHED ? match.getScoreVisitor() : null,
                winner != null ? winner.getId() : null,
                winner != null ? winner.getTeamName() : null,
                status.name()
        );
    }

    private static Team toModelTeam(TeamEntity teamEntity) {
        if (teamEntity == null) return null;
        Team team = new Team();
        team.setId(teamEntity.getId());
        team.setTeamName(teamEntity.getTeamName());
        return team;
    }
}