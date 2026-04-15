package com.techcup.techcup_futbol.controller.mapper;

import com.techcup.techcup_futbol.controller.dto.MatchEventResponse;
import com.techcup.techcup_futbol.controller.dto.MatchResponse;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.MatchEvent;

import java.util.List;

public class MatchMapper {

    private MatchMapper() {}

    public static MatchResponse toResponse(Match m, List<MatchEvent> events) {
        List<MatchEventResponse> eventResponses = events.stream()
                .map(e -> new MatchEventResponse(
                        e.getId(), e.getType(), e.getMinute(),
                        e.getPlayer() != null ? e.getPlayer().getId() : null,
                        e.getPlayer() != null ? e.getPlayer().getFullname() : null
                )).toList();

        return new MatchResponse(
                m.getId(),
                m.getLocalTeam().getId(),  m.getLocalTeam().getTeamName(),
                m.getVisitorTeam().getId(), m.getVisitorTeam().getTeamName(),
                m.getDateTime(),
                m.getScoreLocal(), m.getScoreVisitor(),
                m.getYellowCards(), m.getRedCards(),
                m.getField(), m.getStatus().name(), eventResponses
        );
    }
}
