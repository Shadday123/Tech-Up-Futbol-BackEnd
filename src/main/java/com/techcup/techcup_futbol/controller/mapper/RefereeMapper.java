package com.techcup.techcup_futbol.controller.mapper;

import com.techcup.techcup_futbol.controller.dto.AssignedMatchDTO;
import com.techcup.techcup_futbol.controller.dto.RefereeResponse;
import com.techcup.techcup_futbol.core.model.Referee;

import java.util.List;

public class RefereeMapper {

    private RefereeMapper() {}

    public static RefereeResponse toResponse(Referee r) {
        List<AssignedMatchDTO> assignedMatches = r.getAssignedMatches() == null ? List.of()
                : r.getAssignedMatches().stream().map(m -> new AssignedMatchDTO(
                        m.getId(),
                        m.getLocalTeam().getTeamName(),
                        m.getVisitorTeam().getTeamName(),
                        m.getDateTime(),
                        String.valueOf(m.getField())
                )).toList();

        return new RefereeResponse(r.getId(), r.getFullname(), r.getEmail(), assignedMatches);
    }
}
