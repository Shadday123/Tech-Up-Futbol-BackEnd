package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.validator.TeamValidator;
import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.mapper.TeamPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamServiceImpl(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public Team createTeam(Team team) {
        String ts = LocalDateTime.now().format(FMT);
        team.setId(IdGenerator.generateId());
        log.info("[{}] Creando equipo: {} | ID: {}", ts, team.getTeamName(), team.getId());

        TeamValidator.validateTeamName(team.getTeamName(), getAllTeams());
        TeamValidator.validateCaptain(team);

        // No insertar jugadores al crear — se agregan después via invitePlayer
        team.setPlayers(new ArrayList<>());

        TeamEntity entity = TeamPersistenceMapper.toEntity(team);
        TeamEntity saved = teamRepository.save(entity);

        log.info("Equipo creado — ID: {} | Capitán: {}",
                saved.getId(),
                team.getCaptain() != null ? team.getCaptain().getFullname() : "N/A");

        return TeamPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void invitePlayer(String teamId, Player player) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Invitando jugador '{}' al equipo ID: {}", ts, player.getFullname(), teamId);

        Team team = obtenerPorId(teamId);
        TeamValidator.validatePlayerAddition(team, player);

        if (!player.isDisponible()) {
            throw new TeamException("disponibilidad",
                    String.format(TeamException.PLAYER_NOT_AVAILABLE, player.getFullname()));
        }

        TeamEntity entity = teamRepository.findById(teamId).orElseThrow();
        playerRepository.findById(player.getId()).ifPresent(p -> entity.getPlayers().add(p));
        teamRepository.save(entity);

        log.info("Jugador '{}' agregado al equipo '{}' — Total jugadores: {}",
                player.getFullname(), team.getTeamName(), entity.getPlayers().size());
    }

    @Override
    @Transactional
    public void removePlayer(String teamId, String playerId) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Removiendo jugador ID: '{}' del equipo ID: '{}'", ts, playerId, teamId);

        Team team = obtenerPorId(teamId);

        if (team.getPlayers() == null || team.getPlayers().isEmpty()) {
            throw new TeamException("players",
                    String.format(TeamException.PLAYERS_LIST_EMPTY, team.getTeamName()));
        }

        Player jugador = team.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new TeamException("player",
                        String.format(TeamException.PLAYER_NOT_IN_TEAM, playerId, team.getTeamName())));

        if (team.getCaptain() != null && team.getCaptain().getId().equals(playerId)) {
            throw new TeamException("captain",
                    String.format(TeamException.CANNOT_REMOVE_CAPTAIN, jugador.getFullname()));
        }

        if (team.getPlayers().size() <= 1) {
            throw new TeamException("players",
                    String.format(TeamException.TEAM_REQUIRES_PLAYERS, team.getTeamName()));
        }

        TeamEntity entity = teamRepository.findById(teamId).orElseThrow();
        teamRepository.save(entity);

        log.info("Jugador '{}' removido del equipo '{}' — Jugadores restantes: {}",
                jugador.getFullname(), team.getTeamName(), team.getPlayers().size());
    }

    @Override
    @Transactional
    public void validateTeamForTournament(Team team) {
        log.info("Validando equipo '{}' para inscripción en torneo.", team.getTeamName());
        TeamValidator.validate(team, getAllTeams());
        log.info("Equipo '{}' validado correctamente.", team.getTeamName());
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Optional<Team> buscarPorId(String id) {
        log.info("[{}] Buscando equipo con ID: {}", LocalDateTime.now().format(FMT), id);
        return teamRepository.findById(id).map(TeamPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Team obtenerPorId(String id) {
        return buscarPorId(id).orElseThrow(() ->
                new TeamException("id", String.format(TeamException.TEAM_NOT_FOUND, id)));
    }

    @Override
    @Transactional
    public void deleteTeam(String id) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Eliminando equipo con ID: {}", ts, id);

        Team equipo = obtenerPorId(id);
        teamRepository.deleteById(id);

        log.info("Equipo '{}' eliminado.", equipo.getTeamName());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTeamName(String teamName) {
        return teamRepository.existsByTeamName(teamName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> findByCaptainId(String captainId) {
        return teamRepository.findByCaptainId(captainId).stream()
                .map(TeamPersistenceMapper::toDomain)
                .toList();
    }
}