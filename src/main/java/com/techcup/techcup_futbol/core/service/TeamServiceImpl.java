package com.techcup.techcup_futbol.core.service;

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

@Service
public class TeamServiceImpl implements TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<Team> teams = new ArrayList<>();

    // ── CREATE

    @Override
    public Team createTeam(Team team) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Creando equipo: {}", ts, team.getTeamName());

        TeamValidator.validateTeamName(team.getTeamName(), teams);
        TeamValidator.validateCaptain(team);

        teams.add(team);
        log.info("Equipo creado — ID: {} | Capitán: {} | Total equipos: {}",
                team.getId(),
                team.getCaptain().getFullname(),
                teams.size());

        return team;
    }

    // ── INVITE PLAYER

    @Override
    public void invitePlayer(String teamId, Player player) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Invitando jugador '{}' al equipo ID: {}",
                ts, player.getFullname(), teamId);

        // FIX: usa obtenerPorId que lanza excepción — ya no retorna null
        Team team = obtenerPorId(teamId);

        TeamValidator.validatePlayerAddition(team, player);

        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }

        team.getPlayers().add(player);
        player.setHaveTeam(true);   // setter explícito, no toggle

        log.info("Jugador '{}' agregado al equipo '{}' — Total jugadores: {}",
                player.getFullname(), team.getTeamName(), team.getPlayers().size());
    }

    // ── REMOVE PLAYER

    @Override
    public void removePlayer(String teamId, String playerId) {
        String ts = LocalDateTime.now().format(formatter);
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



    @Override
    public void validateTeamForTournament(Team team) {
        log.info("Validando equipo '{}' para inscripción en torneo.", team.getTeamName());
        TeamValidator.validate(team, teams);
        log.info("Equipo '{}' validado correctamente.", team.getTeamName());
    }

    // ── READ — LISTAR TODOS

    @Override
    public List<Team> getAllTeams() {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Listando todos los equipos — total: {}", ts, teams.size());

        if (teams.isEmpty()) {
            log.info("No hay equipos registrados en el sistema.");
        } else {
            teams.forEach(t -> {
                int n = t.getPlayers() != null ? t.getPlayers().size() : 0;
                String cap = t.getCaptain() != null ? t.getCaptain().getFullname() : "N/A";
                log.debug("  → {} (ID: {}, Jugadores: {}, Capitán: {})",
                        t.getTeamName(), t.getId(), n, cap);
            });
        }
        return new ArrayList<>(teams);
    }

    // ── READ


    public Optional<Team> buscarPorId(String id) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Buscando equipo con ID: {}", ts, id);

        Optional<Team> resultado = teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();

        if (resultado.isPresent()) {
            Team t = resultado.get();
            int n = t.getPlayers() != null ? t.getPlayers().size() : 0;
            String cap = t.getCaptain() != null ? t.getCaptain().getFullname() : "N/A";
            log.info("Equipo encontrado — Nombre: {} | Jugadores: {} | Capitán: {}",
                    t.getTeamName(), n, cap);
        } else {
            log.warn("No existe equipo con ID: {}", id);
        }

        return resultado;
    }

    // ── READ


    @Override
    public Team obtenerPorId(String id) {
        return buscarPorId(id).orElseThrow(() ->
                new TeamException("id",
                        String.format(TeamException.TEAM_NOT_FOUND, id))
        );
    }

    // ── DELETE


    @Override
    public void deleteTeam(String id) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Eliminando equipo con ID: {}", ts, id);

        Team equipo = obtenerPorId(id);

        if (equipo.getPlayers() != null) {
            equipo.getPlayers().forEach(p -> {
                p.setHaveTeam(false);
                log.debug("Jugador '{}' desvinculado del equipo.", p.getFullname());
            });
            log.info("Jugadores desvinculados: {}", equipo.getPlayers().size());
        }

        teams.removeIf(t -> t.getId().equals(id));
        log.info("Equipo '{}' eliminado. Total equipos restantes: {}",
                equipo.getTeamName(), teams.size());
    }
}