package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.LineupException;
import com.techcup.techcup_futbol.repository.LineupRepository;
import com.techcup.techcup_futbol.repository.MatchRepository;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
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
    public Lineup create(String matchId, String teamId, String formation,
                         List<String> starterIds, List<String> substituteIds,
                         List<String> fieldPositions) {
        log.info("Creando alineación — partido: {} | equipo: {}", matchId, teamId);

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new LineupException("matchId",
                        String.format(LineupException.MATCH_NOT_FOUND, matchId)));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new LineupException("teamId",
                        String.format(LineupException.TEAM_NOT_FOUND, teamId)));

        if (lineupRepository.existsByMatchIdAndTeamId(matchId, teamId)) {
            throw new LineupException("lineup",
                    String.format(LineupException.LINEUP_ALREADY_EXISTS,
                            matchId, team.getTeamName()));
        }

        if (starterIds == null || starterIds.size() != 7) {
            throw new LineupException("starters",
                    String.format(LineupException.WRONG_STARTERS_COUNT,
                            starterIds == null ? 0 : starterIds.size()));
        }

        List<Player> teamPlayers = team.getPlayers() != null ? team.getPlayers() : List.of();
        Set<String> teamPlayerIds = new HashSet<>();
        for (Player p : teamPlayers) teamPlayerIds.add(p.getId());

        List<Player> starters = new ArrayList<>();
        for (String sid : starterIds) {
            if (!teamPlayerIds.contains(sid)) {
                throw new LineupException("starters",
                        String.format(LineupException.PLAYER_NOT_IN_TEAM, sid, team.getTeamName()));
            }
            starters.add(playerRepository.findById(sid)
                    .orElseThrow(() -> new LineupException("starters",
                            String.format(LineupException.PLAYER_NOT_IN_TEAM, sid, team.getTeamName()))));
        }

        List<Player> substitutes = new ArrayList<>();
        if (substituteIds != null) {
            for (String sid : substituteIds) {
                playerRepository.findById(sid).ifPresent(substitutes::add);
            }
        }

        Lineup lineup = new Lineup();
        lineup.setId(IdGenerator.generateId());
        lineup.setMatch(match);
        lineup.setTeam(team);
        lineup.setFormation(formation);
        lineup.setStarters(starters);
        lineup.setSubstitutes(substitutes);
        lineup.setFieldPositions(fieldPositions != null ? fieldPositions : List.of());

        lineupRepository.save(lineup);
        log.info("Alineación creada ID: {} para equipo '{}'", lineup.getId(), team.getTeamName());
        return lineup;
    }

    @Override
    public Lineup findByMatchAndTeam(String matchId, String teamId) {
        return lineupRepository.findByMatchIdAndTeamId(matchId, teamId)
                .orElseThrow(() -> new LineupException("lineup",
                        String.format(LineupException.LINEUP_NOT_FOUND, matchId, teamId)));
    }

    @Override
    public Lineup findRivalLineup(String matchId, String myTeamId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new LineupException("matchId",
                        String.format(LineupException.MATCH_NOT_FOUND, matchId)));

        String rivalId = match.getLocalTeam().getId().equals(myTeamId)
                ? match.getVisitorTeam().getId()
                : match.getLocalTeam().getId();

        return lineupRepository.findByMatchIdAndTeamId(matchId, rivalId)
                .orElseThrow(() -> new LineupException("lineup", LineupException.RIVAL_LINEUP_NOT_PUBLISHED));
    }
}
