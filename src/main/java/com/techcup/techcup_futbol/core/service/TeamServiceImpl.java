package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.validator.TeamValidator;
import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.mapper.TeamPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techcup.techcup_futbol.core.util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;


    @Override
    public Team createTeam(Team team) {
        String ts = LocalDateTime.now().format(FMT);

        team.setId(IdGenerator.generateId());

        log.info("[{}] Creando equipo: {} | ID: {}", ts, team.getTeamName(), team.getId());

        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }

        TeamValidator.validateTeamName(team.getTeamName(), getAllTeams());
        TeamValidator.validateCaptain(team);

        team.getPlayers().forEach(p -> {
            p.setHaveTeam(true);
            log.debug("Jugador '{}' vinculado al nuevo equipo '{}'.",
                    p.getFullname(), team.getTeamName());
        });

        TeamEntity entity = TeamPersistenceMapper.toEntity(team);
        teamRepository.save(entity);

        log.info("Equipo creado — ID: {} | Capitán: {} | Jugadores: {}",
                team.getId(),
                team.getCaptain() != null ? team.getCaptain().getFullname() : "N/A",
                team.getPlayers().size());

        return team;
    }


    @Override
    public void invitePlayer(String teamId, Player player) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Invitando jugador '{}' al equipo ID: {}",
                ts, player.getFullname(), teamId);

        Team team = obtenerPorId(teamId);
        TeamValidator.validatePlayerAddition(team, player);

        if (!player.isDisponible()) {
            throw new TeamException("disponibilidad",
                    String.format(TeamException.PLAYER_NOT_AVAILABLE, player.getFullname()));
        }

        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }

        team.getPlayers().add(player);
        player.setHaveTeam(true);

        TeamEntity entity = TeamPersistenceMapper.toEntity(team);
        teamRepository.save(entity);

        log.info("Jugador '{}' agregado al equipo '{}' — Total jugadores: {}",
                player.getFullname(), team.getTeamName(), team.getPlayers().size());
    }


    @Override
    public void removePlayer(String teamId, String playerId) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Removiendo jugador ID: '{}' del equipo ID: '{}'",
                ts, playerId, teamId);

        Team team = obtenerPorId(teamId);

        if (team.getPlayers() == null || team.getPlayers().isEmpty()) {
            throw new TeamException("players",
                    String.format(TeamException.PLAYERS_LIST_EMPTY, team.getTeamName()));
        }

        Player jugador = team.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new TeamException("player",
                        String.format(TeamException.PLAYER_NOT_IN_TEAM,
                                playerId, team.getTeamName())));

        if (team.getCaptain() != null && team.getCaptain().getId().equals(playerId)) {
            throw new TeamException("captain",
                    String.format(TeamException.CANNOT_REMOVE_CAPTAIN, jugador.getFullname()));
        }

        if (team.getPlayers().size() <= 1) {
            throw new TeamException("players",
                    String.format(TeamException.TEAM_REQUIRES_PLAYERS, team.getTeamName()));
        }

        team.getPlayers().remove(jugador);
        jugador.setHaveTeam(false);

        TeamEntity entity = TeamPersistenceMapper.toEntity(team);
        teamRepository.save(entity);

        log.info("Jugador '{}' removido del equipo '{}' — Jugadores restantes: {}",
                jugador.getFullname(), team.getTeamName(), team.getPlayers().size());
    }


    @Override
    public void validateTeamForTournament(Team team) {
        log.info("Validando equipo '{}' para inscripción en torneo.", team.getTeamName());
        TeamValidator.validate(team, getAllTeams());
        log.info("Equipo '{}' validado correctamente.", team.getTeamName());
    }


    @Override
    public List<Team> getAllTeams() {
        List<Team> equipos = teamRepository.findAll()
                .stream()
                .map(TeamPersistenceMapper::toDomain)
                .collect(Collectors.toList());
        log.info("[{}] Listando todos los equipos — total: {}",
                LocalDateTime.now().format(FMT), equipos.size());
        return equipos;
    }

    @Override
    public Optional<Team> buscarPorId(String id) {
        log.info("[{}] Buscando equipo con ID: {}", LocalDateTime.now().format(FMT), id);
        return teamRepository.findById(id).map(TeamPersistenceMapper::toDomain);
    }

    @Override
    public Team obtenerPorId(String id) {
        return buscarPorId(id).orElseThrow(() ->
                new TeamException("id", String.format(TeamException.TEAM_NOT_FOUND, id)));
    }


    @Override
    public void deleteTeam(String id) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Eliminando equipo con ID: {}", ts, id);

        Team equipo = obtenerPorId(id);

        if (equipo.getPlayers() != null) {
            equipo.getPlayers().forEach(p -> {
                p.setHaveTeam(false);
                log.debug("Jugador '{}' desvinculado del equipo.", p.getFullname());
            });
            log.info("Jugadores desvinculados: {}", equipo.getPlayers().size());
        }
        teamRepository.deleteById(id);
        log.info("Equipo '{}' eliminado.", equipo.getTeamName());
    }
}