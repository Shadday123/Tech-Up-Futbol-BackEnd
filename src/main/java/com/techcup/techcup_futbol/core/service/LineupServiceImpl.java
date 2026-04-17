package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Lineup;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.exception.LineupException;
import com.techcup.techcup_futbol.core.util.IdGenerator;

import com.techcup.techcup_futbol.persistence.entity.LineUpEntity;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;

import com.techcup.techcup_futbol.persistence.mapper.LineupPersistenceMapper;

import com.techcup.techcup_futbol.persistence.repository.LineupRepository;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

@Service
public class LineupServiceImpl implements LineupService {

    private static final Logger log = LoggerFactory.getLogger(LineupServiceImpl.class);

    private final LineupRepository lineupRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public LineupServiceImpl(LineupRepository lineupRepository,
                             MatchRepository matchRepository,
                             TeamRepository teamRepository,
                             PlayerRepository playerRepository) {
        this.lineupRepository = lineupRepository;
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public Lineup create(String matchId, String teamId, String formation,
                         List<String> starterIds, List<String> substituteIds,
                         List<String> fieldPositions) {

        log.info("Creando alineación — partido: {} | equipo: {}", matchId, teamId);

        MatchEntity matchEntity = matchRepository.findById(matchId)
                .orElseThrow(() -> new LineupException("matchId",
                        String.format(LineupException.MATCH_NOT_FOUND, matchId)));

        TeamEntity teamEntity = teamRepository.findById(teamId)
                .orElseThrow(() -> new LineupException("teamId",
                        String.format(LineupException.TEAM_NOT_FOUND, teamId)));

        if (lineupRepository.existsByMatchIdAndTeamId(matchId, teamId)) {
            throw new LineupException("lineup",
                    String.format(LineupException.LINEUP_ALREADY_EXISTS,
                            matchId, teamEntity.getTeamName()));
        }

        if (starterIds == null || starterIds.size() != 7) {
            throw new LineupException("starters",
                    String.format(LineupException.WRONG_STARTERS_COUNT,
                            starterIds == null ? 0 : starterIds.size()));
        }

        List<PlayerEntity> starters = starterIds.stream()
                .map(id -> playerRepository.findById(id)
                        .orElseThrow(() -> new LineupException("starters",
                                String.format(LineupException.PLAYER_NOT_IN_TEAM, id, teamEntity.getTeamName()))))
                .toList();

        List<PlayerEntity> substitutes = new ArrayList<>();
        if (substituteIds != null) {
            substitutes = substituteIds.stream()
                    .flatMap(id -> playerRepository.findById(id).stream())
                    .toList();
        }

        LineUpEntity entity = new LineUpEntity();
        entity.setId(IdGenerator.generateId());
        entity.setMatch(matchEntity);
        entity.setTeam(teamEntity);
        entity.setFormation(formation);
        entity.setFieldPositions(fieldPositions != null ? fieldPositions : List.of());
        entity.setStarters(starters);
        entity.setSubstitutes(substitutes);

        LineUpEntity saved = lineupRepository.save(entity);

        log.info("Alineación creada ID: {} para equipo '{}'", saved.getId(), teamEntity.getTeamName());

        return LineupPersistenceMapper.toDomain(saved);
    }

    @Override
    public Lineup findByMatchAndTeam(String matchId, String teamId) {
        LineUpEntity entity = lineupRepository.findByMatchIdAndTeamId(matchId, teamId)
                .orElseThrow(() -> new LineupException("lineup",
                        String.format(LineupException.LINEUP_NOT_FOUND, matchId, teamId)));

        return LineupPersistenceMapper.toDomain(entity);
    }

    @Override
    public Lineup findRivalLineup(String matchId, String myTeamId) {

        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new LineupException("matchId",
                        String.format(LineupException.MATCH_NOT_FOUND, matchId)));

        String rivalId = match.getLocalTeam().getId().equals(myTeamId)
                ? match.getVisitorTeam().getId()
                : match.getLocalTeam().getId();

        LineUpEntity entity = lineupRepository.findByMatchIdAndTeamId(matchId, rivalId)
                .orElseThrow(() -> new LineupException("lineup",
                        LineupException.RIVAL_LINEUP_NOT_PUBLISHED));

        return LineupPersistenceMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void registerMatch(Match match) {
        log.info("Registrando alineaciones del partido: {}", match.getId());
        List<LineUpEntity> lineups = lineupRepository.findByMatchId(match.getId());
        log.info("Encontradas {} alineaciones para el partido", lineups.size());
    }
}
