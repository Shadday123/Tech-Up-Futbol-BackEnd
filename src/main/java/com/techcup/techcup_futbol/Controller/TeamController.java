package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTeamRequest;
import com.techcup.techcup_futbol.Controller.dto.TeamResponse;
import com.techcup.techcup_futbol.core.service.TeamService;
import com.techcup.techcup_futbol.core.model.Team;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: POST /api/teams");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("IP del cliente: {}", getClientIp());
        log.info("Datos del equipo:");
        log.info("  - Nombre: {}", request.getTeamName());
        log.info("  - URL Escudo: {}", request.getShieldUrl());
        log.info("  - Colores: {}", request.getUniformColors());
        log.info("  - ID Capitán: {}", request.getCaptainId());

        try {
            Team teamEntity = new Team();
            teamEntity.setTeamName(request.getTeamName());
            teamEntity.setShieldUrl(request.getShieldUrl());
            teamEntity.setUniformColors(request.getUniformColors());

            Team savedTeam = teamService.createTeam(teamEntity);

            TeamResponse response = new TeamResponse(
                    savedTeam.getId(),
                    savedTeam.getTeamName(),
                    savedTeam.getShieldUrl(),
                    savedTeam.getUniformColors(),
                    savedTeam.getCaptain() != null ? savedTeam.getCaptain().getFullname() : null,
                    savedTeam.getPlayers() != null ?
                            savedTeam.getPlayers().stream()
                                    .map(p -> p.getId())
                                    .collect(Collectors.toList()) :
                            List.of()
            );

            log.info("Equipo creado exitosamente");
            log.info("ID asignado: {}", savedTeam.getId());
            log.info("Respuesta: HTTP 201 Created");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error en la creación del equipo");
            log.error("Excepción: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> findAll() {
        String timestamp = LocalDateTime.now().format(formatter);
        log.info("ENDPOINT: GET /api/teams");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("Acción: Listar todos los equipos");

        try {
            List<Team> teams = teamService.getAllTeams();

            List<TeamResponse> response = teams.stream()
                    .map(t -> new TeamResponse(
                            t.getId(),
                            t.getTeamName(),
                            t.getShieldUrl(),
                            t.getUniformColors(),
                            t.getCaptain() != null ? t.getCaptain().getFullname() : null,
                            t.getPlayers() != null ?
                                    t.getPlayers().stream()
                                            .map(p -> p.getId())
                                            .collect(Collectors.toList()) :
                                    List.of()
                    ))
                    .collect(Collectors.toList());

            log.info("Listado completado");
            log.info("Total de equipos: {}", response.size());
            log.info("Respuesta: HTTP 200 OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("✗ Error al listar equipos");
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: GET /api/teams/{id}");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("ID del equipo solicitado: {}", id);

        try {
            Team team = teamService.getTeamById(id);

            if (team == null) {
                log.warn("Equipo no encontrado");
                log.warn("ID solicitado: {}", id);
                return ResponseEntity.notFound().build();
            }

            TeamResponse response = new TeamResponse(
                    team.getId(),
                    team.getTeamName(),
                    team.getShieldUrl(),
                    team.getUniformColors(),
                    team.getCaptain() != null ? team.getCaptain().getFullname() : null,
                    team.getPlayers() != null ?
                            team.getPlayers().stream()
                                    .map(p -> p.getId())
                                    .collect(Collectors.toList()) :
                            List.of()
            );

            log.info(" Equipo encontrado");
            log.info("Nombre: {}", team.getTeamName());
            int numJugadores = team.getPlayers() != null ? team.getPlayers().size() : 0;
            log.info("Número de jugadores: {}", numJugadores);
            log.info("Respuesta: HTTP 200 OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al buscar equipo");
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Método auxiliar para obtener IP del cliente
    private String getClientIp() {
        return "127.0.0.1"; // En producción obtener del request
    }
}