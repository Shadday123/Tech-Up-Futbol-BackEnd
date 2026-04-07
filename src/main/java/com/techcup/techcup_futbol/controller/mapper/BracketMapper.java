package com.techcup.techcup_futbol.controller.mapper;

import com.techcup.techcup_futbol.controller.dto.BracketMatchDTO;
import com.techcup.techcup_futbol.controller.dto.BracketResponse;
import com.techcup.techcup_futbol.controller.dto.PhaseDTO;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.MatchStatus;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentBrackets;

import java.util.List;

public class BracketMapper {

    private BracketMapper() {}

    public static BracketResponse toResponse(String tournamentId, Tournament tournament,
                                              List<TournamentBrackets> phases) {
        List<PhaseDTO> phaseDTOs = phases.stream().map(b -> {
            List<BracketMatchDTO> matchDTOs = b.getMatches() == null ? List.of()
                    : b.getMatches().stream().map(m -> {
                Team w = m.getWinner();
                MatchStatus st = m.getStatus() != null ? m.getStatus() : MatchStatus.SCHEDULED;
                return new BracketMatchDTO(
                        m.getId(),
                        m.getLocalTeam().getId(),   m.getLocalTeam().getTeamName(),
                        m.getVisitorTeam().getId(), m.getVisitorTeam().getTeamName(),
                        st == MatchStatus.FINISHED ? m.getScoreLocal()   : null,
                        st == MatchStatus.FINISHED ? m.getScoreVisitor() : null,
                        w != null ? w.getId()       : null,
                        w != null ? w.getTeamName() : null,
                        st.name()
                );
            }).toList();
            return new PhaseDTO(b.getPhase().name(), matchDTOs);
        }).toList();

        return new BracketResponse(tournamentId, tournament.getName(), phaseDTOs);
    }
}
