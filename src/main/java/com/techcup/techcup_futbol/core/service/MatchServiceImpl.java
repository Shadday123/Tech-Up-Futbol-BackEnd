package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.MatchException;
import com.techcup.techcup_futbol.repository.MatchEventRepository;
import com.techcup.techcup_futbol.repository.MatchRepository;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchServiceImpl.class);

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchEventRepository matchEventRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    @Lazy
    private LineupService lineupService;

    @Autowired
    private StandingsService standingsService;

    // ── CREATE

    @Override
    @Transactional
    public Match create(String localTeamId, String visitorTeamId, LocalDateTime dateTime,
                        String refereeId, int field) {
        log.info("Creando partido: {} vs {}", localTeamId, visitorTeamId);

        Team local = teamRepository.findById(localTeamId)
                .orElseThrow(() -> new MatchException("localTeamId",
                        String.format(MatchException.TEAM_NOT_FOUND, localTeamId)));

        Team visitor = teamRepository.findById(visitorTeamId)
                .orElseThrow(() -> new MatchException("visitorTeamId",
                        String.format(MatchException.TEAM_NOT_FOUND, visitorTeamId)));

        if (localTeamId.equals(visitorTeamId)) {
            throw new MatchException("teams", MatchException.SAME_TEAM);
        }

        Match match = new Match();
        match.setId(IdGenerator.generateId());
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);
        match.setDateTime(dateTime);
        match.setField(field);
        match.setStatus(MatchStatus.SCHEDULED);

        matchRepository.save(match);
        lineupService.registerMatch(match);

        log.info("Partido creado ID: {} — {} vs {}", match.getId(),
                local.getTeamName(), visitor.getTeamName());
        return match;
    }

    // ── REGISTER RESULT

    @Override
    @Transactional
    public Match registerResult(String matchId, int scoreLocal, int scoreVisitor,
                                List<MatchEventInput> events) {
        log.info("Registrando resultado del partido ID: {}", matchId);

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchException("matchId",
                        String.format(MatchException.MATCH_NOT_FOUND, matchId)));

        if (match.getStatus() == MatchStatus.FINISHED) {
            throw new MatchException("status", MatchException.RESULT_ALREADY_REGISTERED);
        }

        if (events != null) {
            long localGoals = events.stream()
                    .filter(e -> "GOAL".equalsIgnoreCase(e.type()))
                    .filter(e -> isPlayerInTeam(e.playerId(), match.getLocalTeam()))
                    .count();
            long visitorGoals = events.stream()
                    .filter(e -> "GOAL".equalsIgnoreCase(e.type()))
                    .filter(e -> isPlayerInTeam(e.playerId(), match.getVisitorTeam()))
                    .count();

            if (localGoals != scoreLocal) {
                throw new MatchException("events",
                        String.format(MatchException.GOALS_MISMATCH,
                                match.getLocalTeam().getTeamName(),
                                localGoals, scoreLocal));
            }
            if (visitorGoals != scoreVisitor) {
                throw new MatchException("events",
                        String.format(MatchException.GOALS_MISMATCH,
                                match.getVisitorTeam().getTeamName(),
                                visitorGoals, scoreVisitor));
            }

            matchEventRepository.deleteByMatchId(matchId);

            int yellowCount = 0, redCount = 0;
            for (MatchEventInput er : events) {
                Player player = playerRepository.findById(er.playerId())
                        .orElseThrow(() -> new MatchException("events",
                                String.format(MatchException.PLAYER_NOT_IN_LINEUP, er.playerId())));

                MatchEvent event = new MatchEvent();
                event.setId(IdGenerator.generateId());
                event.setType(er.type());
                event.setMinute(er.minute());
                event.setPlayer(player);
                event.setMatch(match);
                matchEventRepository.save(event);

                if ("YELLOW_CARD".equalsIgnoreCase(er.type())) yellowCount++;
                if ("RED_CARD".equalsIgnoreCase(er.type())) redCount++;
            }
            match.setYellowCards(yellowCount);
            match.setRedCards(redCount);
        }

        match.setScoreLocal(scoreLocal);
        match.setScoreVisitor(scoreVisitor);
        match.setStatus(MatchStatus.FINISHED);
        matchRepository.save(match);

        standingsService.updateFromMatch(match);

        log.info("Resultado registrado — {} {} : {} {}",
                match.getLocalTeam().getTeamName(), match.getScoreLocal(),
                match.getScoreVisitor(), match.getVisitorTeam().getTeamName());
        return match;
    }

    // ── READ

    @Override
    public Match findById(String matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchException("matchId",
                        String.format(MatchException.MATCH_NOT_FOUND, matchId)));
    }

    @Override
    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    @Override
    public List<Match> findByTeamId(String teamId) {
        return matchRepository.findByLocalTeamIdOrVisitorTeamId(teamId, teamId);
    }

    @Override
    public boolean isResultRegistered(String matchId) {
        return matchRepository.findById(matchId)
                .map(m -> m.getStatus() == MatchStatus.FINISHED)
                .orElse(false);
    }

    @Override
    @Transactional
    public void registerMatch(Match match) {
        if (!matchRepository.existsById(match.getId())) {
            matchRepository.save(match);
        }
    }

    @Override
    public Map<String, Match> getMatches() {
        return matchRepository.findAll().stream()
                .collect(Collectors.toMap(Match::getId, m -> m));
    }

    private boolean isPlayerInTeam(String playerId, Team team) {
        if (team.getPlayers() == null) return false;
        return team.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId));
    }
}
