package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.MatchDTOs.*;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.exception.MatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchServiceImpl.class);

    private final Map<String, Match> matches = new HashMap<>();
    private final Map<String, List<MatchEvent>> matchEvents = new HashMap<>();
    private final Map<String, MatchStatus> matchStatusMap = new HashMap<>();

    @Autowired
    private LineupServiceImpl lineupService;

    @Autowired
    private StandingsServiceImpl standingsService;

    @Override
    public MatchResponse create(CreateMatchRequest request) {
        log.info("Creando partido: {} vs {}", request.localTeamId(), request.visitorTeamId());

        Team local = DataStore.equipos.get(request.localTeamId());
        if (local == null) {
            throw new MatchException("localTeamId",
                    String.format(MatchException.TEAM_NOT_FOUND, request.localTeamId()));
        }
        Team visitor = DataStore.equipos.get(request.visitorTeamId());
        if (visitor == null) {
            throw new MatchException("visitorTeamId",
                    String.format(MatchException.TEAM_NOT_FOUND, request.visitorTeamId()));
        }
        if (request.localTeamId().equals(request.visitorTeamId())) {
            throw new MatchException("teams", MatchException.SAME_TEAM);
        }

        Match match = new Match();
        match.setId(UUID.randomUUID().toString());
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);
        match.setDateTime(request.dateTime());
        match.setField(request.field());

        matches.put(match.getId(), match);
        matchStatusMap.put(match.getId(), MatchStatus.SCHEDULED);
        matchEvents.put(match.getId(), new ArrayList<>());

        lineupService.registerMatch(match);

        log.info("Partido creado ID: {} — {} vs {}", match.getId(),
                local.getTeamName(), visitor.getTeamName());
        return toResponse(match);
    }

    @Override
    public MatchResponse registerResult(String matchId, RegisterResultRequest request) {
        log.info("Registrando resultado del partido ID: {}", matchId);

        Match match = matches.get(matchId);
        if (match == null) {
            throw new MatchException("matchId",
                    String.format(MatchException.MATCH_NOT_FOUND, matchId));
        }
        if (matchStatusMap.get(matchId) == MatchStatus.FINISHED) {
            throw new MatchException("status", MatchException.RESULT_ALREADY_REGISTERED);
        }

        if (request.events() != null) {
            long localGoals = request.events().stream()
                    .filter(e -> "GOAL".equalsIgnoreCase(e.type()))
                    .filter(e -> isPlayerInTeam(e.playerId(), match.getLocalTeam()))
                    .count();
            long visitorGoals = request.events().stream()
                    .filter(e -> "GOAL".equalsIgnoreCase(e.type()))
                    .filter(e -> isPlayerInTeam(e.playerId(), match.getVisitorTeam()))
                    .count();

            if (localGoals != request.scoreLocal()) {
                throw new MatchException("events",
                        String.format(MatchException.GOALS_MISMATCH,
                                match.getLocalTeam().getTeamName(), localGoals, request.scoreLocal()));
            }
            if (visitorGoals != request.scoreVisitor()) {
                throw new MatchException("events",
                        String.format(MatchException.GOALS_MISMATCH,
                                match.getVisitorTeam().getTeamName(), visitorGoals, request.scoreVisitor()));
            }

            List<MatchEvent> events = new ArrayList<>();
            int yellowCount = 0, redCount = 0;
            for (MatchEventRequest er : request.events()) {
                Player player = DataStore.jugadores.get(er.playerId());
                if (player == null) {
                    throw new MatchException("events",
                            String.format(MatchException.PLAYER_NOT_IN_LINEUP, er.playerId()));
                }
                MatchEvent event = new MatchEvent();
                event.setId(UUID.randomUUID().toString());
                event.setType(er.type());
                event.setMinute(er.minute());
                event.setPlayer(player);
                event.setMatch(match);
                events.add(event);
                if ("YELLOW_CARD".equalsIgnoreCase(er.type())) yellowCount++;
                if ("RED_CARD".equalsIgnoreCase(er.type())) redCount++;
            }
            matchEvents.put(matchId, events);
            match.setYellowCards(yellowCount);
            match.setRedCards(redCount);
        }

        match.setScoreLocal(request.scoreLocal());
        match.setScoreVisitor(request.scoreVisitor());
        matchStatusMap.put(matchId, MatchStatus.FINISHED);

        standingsService.updateFromMatch(match);

        log.info("Resultado registrado — {} {} : {} {}",
                match.getLocalTeam().getTeamName(), match.getScoreLocal(),
                match.getScoreVisitor(), match.getVisitorTeam().getTeamName());
        return toResponse(match);
    }

    @Override
    public MatchResponse findById(String matchId) {
        Match match = matches.get(matchId);
        if (match == null) {
            throw new MatchException("matchId",
                    String.format(MatchException.MATCH_NOT_FOUND, matchId));
        }
        return toResponse(match);
    }

    @Override
    public List<MatchResponse> findAll() {
        return matches.values().stream().map(this::toResponse).toList();
    }

    @Override
    public List<MatchResponse> findByTeamId(String teamId) {
        return matches.values().stream()
                .filter(m -> m.getLocalTeam().getId().equals(teamId)
                        || m.getVisitorTeam().getId().equals(teamId))
                .map(this::toResponse)
                .toList();
    }

    public Map<String, Match> getMatches() { return matches; }

    private boolean isPlayerInTeam(String playerId, Team team) {
        if (team.getPlayers() == null) return false;
        return team.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId));
    }

    private MatchResponse toResponse(Match m) {
        List<MatchEventResponse> events = matchEvents.getOrDefault(m.getId(), List.of())
                .stream().map(e -> new MatchEventResponse(
                        e.getId(), e.getType(), e.getMinute(),
                        e.getPlayer() != null ? e.getPlayer().getId() : null,
                        e.getPlayer() != null ? e.getPlayer().getFullname() : null
                )).toList();

        String status = matchStatusMap.getOrDefault(m.getId(), MatchStatus.SCHEDULED).name();

        return new MatchResponse(
                m.getId(),
                m.getLocalTeam().getId(), m.getLocalTeam().getTeamName(),
                m.getVisitorTeam().getId(), m.getVisitorTeam().getTeamName(),
                m.getDateTime(),
                m.getScoreLocal(), m.getScoreVisitor(),
                m.getYellowCards(), m.getRedCards(),
                m.getField(), status, events
        );
    }
}
