package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.LineupDTOs.*;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.exception.LineupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineupServiceImpl implements LineupService {

    private static final Logger log = LoggerFactory.getLogger(LineupServiceImpl.class);

    private final Map<String, Match> matches = new HashMap<>();
    private final Map<String, Lineup> lineups = new HashMap<>();

    public void registerMatch(Match match) {
        matches.put(match.getId(), match);
    }

    @Override
    public LineupResponse create(CreateLineupRequest request) {
        log.info("Creando alineación — partido: {} | equipo: {}", request.matchId(), request.teamId());

        Match match = matches.get(request.matchId());
        if (match == null) {
            throw new LineupException("matchId",
                    String.format(LineupException.MATCH_NOT_FOUND, request.matchId()));
        }

        Team team = DataStore.equipos.get(request.teamId());
        if (team == null) {
            throw new LineupException("teamId",
                    String.format(LineupException.TEAM_NOT_FOUND, request.teamId()));
        }

        String key = request.matchId() + "_" + request.teamId();
        if (lineups.containsKey(key)) {
            throw new LineupException("lineup",
                    String.format(LineupException.LINEUP_ALREADY_EXISTS,
                            request.matchId(), team.getTeamName()));
        }

        if (request.starterIds() == null || request.starterIds().size() != 7) {
            throw new LineupException("starters",
                    String.format(LineupException.WRONG_STARTERS_COUNT,
                            request.starterIds() == null ? 0 : request.starterIds().size()));
        }

        List<Player> teamPlayers = team.getPlayers() != null ? team.getPlayers() : List.of();
        Set<String> teamPlayerIds = new HashSet<>();
        for (Player p : teamPlayers) teamPlayerIds.add(p.getId());

        List<Player> starters = new ArrayList<>();
        for (String sid : request.starterIds()) {
            if (!teamPlayerIds.contains(sid)) {
                throw new LineupException("starters",
                        String.format(LineupException.PLAYER_NOT_IN_TEAM, sid, team.getTeamName()));
            }
            starters.add(DataStore.jugadores.get(sid));
        }

        List<Player> substitutes = new ArrayList<>();
        if (request.substituteIds() != null) {
            for (String sid : request.substituteIds()) {
                Player p = DataStore.jugadores.get(sid);
                if (p != null) substitutes.add(p);
            }
        }

        List<String> positions = request.fieldPositions() == null ? List.of()
                : request.fieldPositions().stream()
                    .map(fp -> fp.playerId() + "|" + fp.x() + "|" + fp.y())
                    .toList();

        Lineup lineup = new Lineup();
        lineup.setId(UUID.randomUUID().toString());
        lineup.setMatch(match);
        lineup.setTeam(team);
        lineup.setFormation(request.formation());
        lineup.setStarters(starters);
        lineup.setSubstitutes(substitutes);
        lineup.setFieldPositions(positions);

        lineups.put(key, lineup);
        log.info("Alineación creada ID: {} para equipo '{}'", lineup.getId(), team.getTeamName());
        return toResponse(lineup);
    }

    @Override
    public LineupResponse findByMatchAndTeam(String matchId, String teamId) {
        String key = matchId + "_" + teamId;
        Lineup lineup = lineups.get(key);
        if (lineup == null) {
            throw new LineupException("lineup",
                    String.format(LineupException.LINEUP_NOT_FOUND, matchId, teamId));
        }
        return toResponse(lineup);
    }

    @Override
    public LineupResponse findRivalLineup(String matchId, String myTeamId) {
        Match match = matches.get(matchId);
        if (match == null) {
            throw new LineupException("matchId",
                    String.format(LineupException.MATCH_NOT_FOUND, matchId));
        }

        String rivalId = match.getLocalTeam().getId().equals(myTeamId)
                ? match.getVisitorTeam().getId()
                : match.getLocalTeam().getId();

        String rivalKey = matchId + "_" + rivalId;
        Lineup rivalLineup = lineups.get(rivalKey);
        if (rivalLineup == null) {
            throw new LineupException("lineup", LineupException.RIVAL_LINEUP_NOT_PUBLISHED);
        }
        return toResponse(rivalLineup);
    }

    private LineupResponse toResponse(Lineup l) {
        List<LineupPlayerDTO> starters = l.getStarters() == null ? List.of()
                : l.getStarters().stream().map(this::toPlayerDTO).toList();

        List<LineupPlayerDTO> subs = l.getSubstitutes() == null ? List.of()
                : l.getSubstitutes().stream().map(this::toPlayerDTO).toList();

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

    private LineupPlayerDTO toPlayerDTO(Player p) {
        return new LineupPlayerDTO(
                p.getId(), p.getFullname(),
                p.getPosition() != null ? p.getPosition().name() : null,
                p.getDorsalNumber(), p.getPhotoUrl()
        );
    }
}
