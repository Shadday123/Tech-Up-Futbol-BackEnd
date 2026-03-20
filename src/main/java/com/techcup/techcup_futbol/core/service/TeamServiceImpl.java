package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.validator.TeamValidator;
import com.techcup.techcup_futbol.exception.TeamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamServiceImpl implements TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    // CREATE

    @Override
    public Team createTeam(Team team) {
        String ts = LocalDateTime.now().format(FMT);

        if (team.getId() == null || team.getId().isBlank()) {
            team.setId(UUID.randomUUID().toString());
        }

        log.info("[{}] Creando equipo: {} | ID: {}", ts, team.getTeamName(), team.getId());

        // Validar nombre único contra todos los equipos existentes
        TeamValidator.validateTeamName(team.getTeamName(), getAllTeams());
        TeamValidator.validateCaptain(team);

        // Asegurarse de que la lista de jugadores esté inicializada
        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }

        DataStore.equipos.put(team.getId(), team);

        log.info("Equipo creado — ID: {} | Capitán: {} | Total equipos: {}",
                team.getId(),
                team.getCaptain().getFullname(),
                DataStore.equipos.size());

        return team;
    }

    //  INVITE PLAYER

    @Override
    public void invitePlayer(String teamId, Player player) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Invitando jugador '{}' al equipo ID: {}",
                ts, player.getFullname(), teamId);

        Team team = obtenerPorId(teamId);
        TeamValidator.validatePlayerAddition(team, player);

        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }

        team.getPlayers().add(player);
        player.setHaveTeam(true);

        log.info("Jugador '{}' agregado al equipo '{}' — Total jugadores: {}",
                player.getFullname(), team.getTeamName(), team.getPlayers().size());
    }

    // REMOVE PLAYER

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

        team.getPlayers().remove(jugador);
        jugador.setHaveTeam(false);

        log.info("Jugador '{}' removido del equipo '{}' — Jugadores restantes: {}",
                jugador.getFullname(), team.getTeamName(), team.getPlayers().size());
    }

    // VALIDATE

    @Override
    public void validateTeamForTournament(Team team) {
        log.info("Validando equipo '{}' para inscripción en torneo.", team.getTeamName());
        TeamValidator.validate(team, getAllTeams());
        log.info("Equipo '{}' validado correctamente.", team.getTeamName());
    }

    // READ

    @Override
    public List<Team> getAllTeams() {
        log.info("[{}] Listando todos los equipos — total: {}",
                LocalDateTime.now().format(FMT), DataStore.equipos.size());
        return new ArrayList<>(DataStore.equipos.values());
    }

    @Override
    public Optional<Team> buscarPorId(String id) {
        log.info("[{}] Buscando equipo con ID: {}", LocalDateTime.now().format(FMT), id);
        Optional<Team> resultado = Optional.ofNullable(DataStore.equipos.get(id));
        if (resultado.isPresent()) {
            Team t = resultado.get();
            log.info("Equipo encontrado — Nombre: {} | Jugadores: {}",
                    t.getTeamName(),
                    t.getPlayers() != null ? t.getPlayers().size() : 0);
        } else {
            log.warn("No existe equipo con ID: {}", id);
        }
        return resultado;
    }

    @Override
    public Team obtenerPorId(String id) {
        return buscarPorId(id).orElseThrow(() ->
                new TeamException("id", String.format(TeamException.TEAM_NOT_FOUND, id)));
    }

    //DELETE

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

        DataStore.equipos.remove(id);
        log.info("Equipo '{}' eliminado. Total equipos restantes: {}",
                equipo.getTeamName(), DataStore.equipos.size());
    }
}