package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.MatchException;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.mapper.MatchPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.MatchEventRepository;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchServiceImpl.class);

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public MatchServiceImpl(MatchRepository matchRepository,
                            MatchEventRepository matchEventRepository,
                            TeamRepository teamRepository,
                            PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.matchEventRepository = matchEventRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public Match create(String localTeamId, String visitorTeamId, LocalDateTime dateTime,
                        String refereeId, int field) {
        log.info("Creando partido: {} vs {}", localTeamId, visitorTeamId);

        TeamEntity localTeam = teamRepository.findById(localTeamId)
                .orElseThrow(() -> new MatchException("localTeamId",
                        String.format(MatchException.TEAM_NOT_FOUND, localTeamId)));

        TeamEntity visitorTeam = teamRepository.findById(visitorTeamId)
                .orElseThrow(() -> new MatchException("visitorTeamId",
                        String.format(MatchException.TEAM_NOT_FOUND, visitorTeamId)));

        if (localTeamId.equals(visitorTeamId)) {
            throw new MatchException("teams", MatchException.SAME_TEAM);
        }

        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setId(IdGenerator.generateId());
        matchEntity.setLocalTeam(localTeam);
        matchEntity.setVisitorTeam(visitorTeam);
        matchEntity.setDateTime(dateTime);
        matchEntity.setField(field);
        matchEntity.setStatus(MatchStatus.SCHEDULED);

        MatchEntity saved = matchRepository.save(matchEntity);
        log.info("Partido creado ID: {} — {} vs {}", saved.getId(),
                localTeam.getTeamName(), visitorTeam.getTeamName());

        return MatchPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Match registerResult(String matchId, int scoreLocal, int scoreVisitor,
                                List<MatchEventInput> events) {
        log.info("Registrando resultado del partido ID: {}", matchId);

        MatchEntity match = matchRepository.findById(matchId)
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
                                match.getLocalTeam().getTeamName(), localGoals, scoreLocal));
            }
            if (visitorGoals != scoreVisitor) {
                throw new MatchException("events",
                        String.format(MatchException.GOALS_MISMATCH,
                                match.getVisitorTeam().getTeamName(), visitorGoals, scoreVisitor));
            }

            matchEventRepository.deleteByMatchId(matchId);

            int yellowCount = 0, redCount = 0;
            for (MatchEventInput er : events) {
                PlayerEntity player = playerRepository.findById(er.playerId())
                        .orElseThrow(() -> new MatchException("events",
                                String.format(MatchException.PLAYER_NOT_IN_LINEUP, er.playerId())));

                // Crear MatchEvent (asumiendo existe el mapper)
                // MatchEvent event = MatchEventPersistenceMapper.toEntity(...);
                // matchEventRepository.save(event);

                if ("YELLOW_CARD".equalsIgnoreCase(er.type())) yellowCount++;
                if ("RED_CARD".equalsIgnoreCase(er.type())) redCount++;
            }
            match.setYellowCards(yellowCount);
            match.setRedCards(redCount);
        }

        match.setScoreLocal(scoreLocal);
        match.setScoreVisitor(scoreVisitor);
        match.setStatus(MatchStatus.FINISHED);
        MatchEntity saved = matchRepository.save(match);

        log.info("Resultado registrado — {} {} : {} {}",
                match.getLocalTeam().getTeamName(), match.getScoreLocal(),
                match.getScoreVisitor(), match.getVisitorTeam().getTeamName());

        return MatchPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Match findById(String matchId) {
        MatchEntity entity = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchException("matchId",
                        String.format(MatchException.MATCH_NOT_FOUND, matchId)));
        return MatchPersistenceMapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Match> findAll() {
        return matchRepository.findAll().stream()
                .map(MatchPersistenceMapper::toDomainShallow)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Match> findByTeamId(String teamId) {
        return matchRepository.findByLocalTeamIdOrVisitorTeamId(teamId, teamId).stream()
                .map(MatchPersistenceMapper::toDomainShallow)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResultRegistered(String matchId) {
        return matchRepository.findById(matchId)
                .map(m -> m.getStatus() == MatchStatus.FINISHED)
                .orElse(false);
    }

    @Override
    @Transactional
    public void registerMatch(MatchEntity match) {
        if (!matchRepository.existsById(match.getId())) {
            matchRepository.save(match);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Match> getMatches() {
        return matchRepository.findAll().stream()
                .map(MatchPersistenceMapper::toDomainShallow)
                .collect(Collectors.toMap(Match::getId, m -> m));
    }

    private boolean isPlayerInTeam(String playerId, TeamEntity team) {
        if (team.getPlayers() == null) return false;
        return team.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId));
    }
}
