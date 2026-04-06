package com.techcup.techcup_futbol.Controller.mapper;

import com.techcup.techcup_futbol.Controller.dto.LineupPlayerDTO;
import com.techcup.techcup_futbol.Controller.dto.LineupResponse;
import com.techcup.techcup_futbol.Controller.dto.PlayerPositionDTO;
import com.techcup.techcup_futbol.core.model.Lineup;
import com.techcup.techcup_futbol.core.model.Player;

import java.util.List;

public class LineupMapper {

    private LineupMapper() {}

    public static LineupResponse toResponse(Lineup l) {
        List<LineupPlayerDTO> starters = l.getStarters() == null ? List.of()
                : l.getStarters().stream().map(LineupMapper::toPlayerDTO).toList();

        List<LineupPlayerDTO> subs = l.getSubstitutes() == null ? List.of()
                : l.getSubstitutes().stream().map(LineupMapper::toPlayerDTO).toList();

        List<PlayerPositionDTO> positions = l.getFieldPositions() == null ? List.of()
                : l.getFieldPositions().stream().map(s -> {
                    String[] p = s.split("\\|", 3);
                    return new PlayerPositionDTO(p[0],
                            p.length > 1 ? Double.parseDouble(p[1]) : 0,
                            p.length > 2 ? Double.parseDouble(p[2]) : 0);
                }).toList();

        return new LineupResponse(
                l.getId(),
                l.getMatch().getId(),
                l.getTeam().getId(),
                l.getTeam().getTeamName(),
                l.getFormation(),
                starters, subs, positions
        );
    }

    public static LineupPlayerDTO toPlayerDTO(Player p) {
        return new LineupPlayerDTO(
                p.getId(), p.getFullname(),
                p.getPosition() != null ? p.getPosition().name() : null,
                p.getDorsalNumber(), p.getPhotoUrl()
        );
    }
}
