package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateLineupRequest;
import com.techcup.techcup_futbol.Controller.dto.LineupResponse;
import com.techcup.techcup_futbol.Controller.mapper.LineupMapper;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.LineupException;
import com.techcup.techcup_futbol.repository.LineupRepository;
import com.techcup.techcup_futbol.repository.MatchRepository;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import com.techcup.techcup_futbol.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LineupServiceImpl implements LineupService {

    private static final Logger log = LoggerFactory.getLogger(LineupServiceImpl.class);

    @Autowired
    private LineupRepository lineupRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    @Transactional
    public void registerMatch(Match match) {
        if (match != null && !matchRepository.existsById(match.getId())) {
            matchRepository.save(match);
        }
    }

    @Override
    @Transactional
    public LineupResponse create(CreateLineupRequest request) {
        log.info("Creando alineación — partido: {} | equipo: {}", request.matchId(), request.teamId());

        Match match = matchRepository.findById(request.matchId())
                .orElseThrow(() -> new LineupException("matchId",
                        String.format(LineupException.MATCH_NOT_FOUND, request.matchId())));

        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new LineupException("teamId",
                        String.format(LineupException.TEAM_NOT_FOUND, request.teamId())));

        if (lineupRepository.existsByMatchIdAndTeamId(request.matchId(), request.teamId())) {
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
            starters.add(playerRepository.findById(sid)
                    .orElseThrow(() -> new LineupException("starters",
                            String.format(LineupException.PLAYER_NOT_IN_TEAM, sid, team.getTeamName()))));
        }

        List<Player> substitutes = new ArrayList<>();
        if (request.substituteIds() != null) {
            for (String sid : request.substituteIds()) {
                playerRepository.findById(sid).ifPresent(substitutes::add);
            }
        }

        List<String> positions = request.fieldPositions() == null ? List.of()
                : request.fieldPositions().stream()
                    .map(fp -> fp.playerId() + "|" + fp.x() + "|" + fp.y())
                    .toList();

        Lineup lineup = new Lineup();
        lineup.setId(IdGenerator.generateId());
        lineup.setMatch(match);
        lineup.setTeam(team);
        lineup.setFormation(request.formation());
        lineup.setStarters(starters);
        lineup.setSubstitutes(substitutes);
        lineup.setFieldPositions(positions);

        lineupRepository.save(lineup);
        log.info("Alineación creada ID: {} para equipo '{}'", lineup.getId(), team.getTeamName());
        return LineupMapper.toResponse(lineup);
    }

    @Override
    public LineupResponse findByMatchAndTeam(String matchId, String teamId) {
        Lineup lineup = lineupRepository.findByMatchIdAndTeamId(matchId, teamId)
                .orElseThrow(() -> new LineupException("lineup",
                        String.format(LineupException.LINEUP_NOT_FOUND, matchId, teamId)));
        return LineupMapper.toResponse(lineup);
    }

    @Override
    public LineupResponse findRivalLineup(String matchId, String myTeamId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new LineupException("matchId",
                        String.format(LineupException.MATCH_NOT_FOUND, matchId)));

        String rivalId = match.getLocalTeam().getId().equals(myTeamId)
                ? match.getVisitorTeam().getId()
                : match.getLocalTeam().getId();

        Lineup rivalLineup = lineupRepository.findByMatchIdAndTeamId(matchId, rivalId)
                .orElseThrow(() -> new LineupException("lineup", LineupException.RIVAL_LINEUP_NOT_PUBLISHED));
        return LineupMapper.toResponse(rivalLineup);
    }
}
