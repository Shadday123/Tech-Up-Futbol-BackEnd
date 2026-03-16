package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.service.TournamentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private static final Logger log = LoggerFactory.getLogger(TournamentController.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: POST /api/tournaments");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("IP del cliente: {}", getClientIp());
        log.info("Datos del torneo:");
        log.info("  - Nombre: {}", request.name());
        log.info("  - Fecha inicio: {}", request.startDate());
        log.info("  - Fecha fin: {}", request.endDate());
        log.info("  - Costo: ${}", request.registrationFee());
        log.info("  - Máximo equipos: {}", request.maxTeams());
        log.info("  - Reglamento: {}", request.rules());

        try {
            TournamentResponse newTournament = tournamentService.create(request);
            log.info("Torneo creado exitosamente");
            log.info("ID asignado: {}", newTournament.id());
            log.info("Estado inicial: {}", newTournament.currentState());
            log.info("Respuesta: HTTP 201 Created");
            return new ResponseEntity<>(newTournament, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error en la creación del torneo");
            log.error("Excepción: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: GET /api/tournaments");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("Acción: Listar todos los torneos");

        try {
            List<TournamentResponse> tournaments = tournamentService.findAll();
            log.info("✓ Listado completado");
            log.info("Total de torneos: {}", tournaments.size());

            // Resumen por estado
            if (!tournaments.isEmpty()) {
                log.debug("Estados de torneos:");
                tournaments.stream()
                        .map(TournamentResponse::currentState)
                        .distinct()
                        .forEach(estado -> {
                            long count = tournaments.stream()
                                    .filter(t -> t.currentState().equals(estado))
                                    .count();
                            log.debug("  - {}: {}", estado, count);
                        });
            }

            log.info("Respuesta: HTTP 200 OK");
            return ResponseEntity.ok(tournaments);
        } catch (Exception e) {
            log.error("Error al listar torneos");
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(@PathVariable String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: GET /api/tournaments/{id}");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("ID del torneo solicitado: {}", id);

        try {
            TournamentResponse tournament = tournamentService.findById(id);
            log.info("Torneo encontrado");
            log.info("Nombre: {}", tournament.name());
            log.info("Estado: {}", tournament.currentState());
            log.info("Costo: ${}", tournament.registrationFee());
            log.info("Máximo equipos: {}", tournament.maxTeams());
            log.info("Respuesta: HTTP 200 OK");
            return ResponseEntity.ok(tournament);
        } catch (Exception e) {
            log.warn("Torneo no encontrado");
            log.warn("ID solicitado: {}", id);
            log.warn("Excepción: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponse> start(@PathVariable String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: PUT /api/tournaments/{id}/start");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("ID del torneo a iniciar: {}", id);
        log.info("Acción: Cambiar estado a ACTIVE");

        try {
            TournamentResponse updated = tournamentService.updateStatus(id, "ACTIVE");
            log.info("Torneo iniciado exitosamente");
            log.info("Nombre: {}", updated.name());
            log.info("Estado anterior: DRAFT");
            log.info("Estado nuevo: {}", updated.currentState());
            log.info("Respuesta: HTTP 200 OK");
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error al iniciar torneo");
            log.error("ID del torneo: {}", id);
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<TournamentResponse> finish(@PathVariable String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: PUT /api/tournaments/{id}/finish");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("ID del torneo a finalizar: {}", id);
        log.info("Acción: Cambiar estado a COMPLETED");

        try {
            TournamentResponse updated = tournamentService.updateStatus(id, "COMPLETED");
            log.info("Torneo finalizado exitosamente");
            log.info("Nombre: {}", updated.name());
            log.info("Estado anterior: IN_PROGRESS");
            log.info("Estado nuevo: {}", updated.currentState());
            log.info("Respuesta: HTTP 200 OK");
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error al finalizar torneo");
            log.error("ID del torneo: {}", id);
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Método auxiliar para obtener IP del cliente
    private String getClientIp() {
        return "127.0.0.1"; // En producción obtener del request
    }
}