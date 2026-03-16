package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<Team> teams = new ArrayList<>();

    // CREATE
    @Override
    public Team createTeam(Team team) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Iniciando creación de equipo", timestamp);
        log.info("Nombre del equipo: {}", team.getTeamName());
        log.info("Colores del uniforme: {}", team.getUniformColors());
        log.info("URL del escudo: {}", team.getShieldUrl());

        if (team.getCaptain() != null) {
            log.info("Capitán asignado: {} (ID: {})",
                    team.getCaptain().getFullname(),
                    team.getCaptain().getId());
        } else {
            log.warn("No hay capitán asignado");
        }

        // Validar nombre único
        if (teams.stream().anyMatch(t -> t.getTeamName().equals(team.getTeamName()))) {
            log.error("Ya existe un equipo con el nombre: {}", team.getTeamName());
            throw new IllegalArgumentException("El nombre del equipo ya existe");
        }

        teams.add(team);

        log.info("Equipo creado exitosamente");
        log.info("ID asignado: {}", team.getId());
        log.info("Total de equipos en el sistema: {}", teams.size());

        return team;
    }

    // INVITE PLAYER
    @Override
    public void invitePlayer(String teamId, Player player) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Invitando jugador a equipo", timestamp);
        log.info("ID del equipo: {}", teamId);
        log.info("Jugador a invitar: {} (ID: {})", player.getFullname(), player.getId());
        log.info("Email del jugador: {}", player.getEmail());
        log.info("Posición: {}", player.getPosition());
        log.info("Dorsal: {}", player.getDorsalNumber());

        Team team = getTeamById(teamId);
        if (team != null) {
            // Validar si el jugador ya tiene equipo
            if (player.isHaveTeam()) {
                log.warn("⚠ El jugador {} ya tiene equipo asignado", player.getFullname());
            }

            if (team.getPlayers() == null) {
                team.setPlayers(new ArrayList<>());
            }

            int jugadoresAntes = team.getPlayers().size();
            team.getPlayers().add(player);
            int jugadoresDespues = team.getPlayers().size();

            log.info("✓ Jugador agregado exitosamente al equipo");
            log.info("Jugadores en el equipo ANTES: {}", jugadoresAntes);
            log.info("Jugadores en el equipo DESPUÉS: {}", jugadoresDespues);

            // Validación de tamaño de equipo
            if (jugadoresDespues > 12) {
                log.error("✗ Advertencia: Equipo excede el máximo de 12 jugadores");
            }
        } else {
            log.error("✗ FALLO: No se encontró equipo con ID: {}", teamId);
        }

    }

    // READ - LISTAR TODOS
    @Override
    public List<Team> getAllTeams() {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Listando todos los equipos del sistema", timestamp);
        log.info("Total de equipos encontrados: {}", teams.size());

        if (!teams.isEmpty()) {
            log.debug("Detalles de los equipos:");
            teams.forEach(team -> {
                int numJugadores = team.getPlayers() != null ? team.getPlayers().size() : 0;
                String captainName = team.getCaptain() != null ? team.getCaptain().getFullname() : "N/A";
                log.debug("  - {} (ID: {}, Jugadores: {}, Capitán: {})",
                        team.getTeamName(), team.getId(), numJugadores, captainName);
            });
        } else {
            log.info(" No hay equipos registrados en el sistema");
        }

        return teams;
    }

    // READ - BUSCAR POR ID
    @Override
    public Team getTeamById(String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Buscando equipo con ID: {}", timestamp, id);

        Team team = teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (team != null) {
            log.info("Nombre del equipo: {}", team.getTeamName());
            log.info("Colores: {}", team.getUniformColors());
            log.info("Escudo: {}", team.getShieldUrl());

            if (team.getCaptain() != null) {
                log.info("Capitán: {} (ID: {})",
                        team.getCaptain().getFullname(),
                        team.getCaptain().getId());
            }

            int numJugadores = team.getPlayers() != null ? team.getPlayers().size() : 0;
            log.info("Número de jugadores: {}", numJugadores);

            if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
                log.debug("Lista de jugadores:");
                team.getPlayers().forEach(p ->
                        log.debug("  - {} (Posición: {}, Dorsal: {})",
                                p.getFullname(), p.getPosition(), p.getDorsalNumber())
                );
            }
        } else {
            log.warn("No existe equipo con ID: {}", id);
        }

        return team;
    }

    // DELETE
    @Override
    public void deleteTeam(String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Intentando eliminar equipo", timestamp);
        log.info("ID del equipo: {}", id);

        Team equipoEncontrado = teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (equipoEncontrado != null) {
            log.info("Equipo encontrado: {}", equipoEncontrado.getTeamName());
            int jugadores = equipoEncontrado.getPlayers() != null ?
                    equipoEncontrado.getPlayers().size() : 0;
            log.info("Jugadores a desvincularse: {}", jugadores);

            teams.removeIf(t -> t.getId().equals(id));
            log.info(" Equipo eliminado exitosamente");
        } else {
            log.warn(" No se puede eliminar - Equipo no encontrado");
        }

        log.info("Total de equipos después de eliminar: {}", teams.size());
    }
}