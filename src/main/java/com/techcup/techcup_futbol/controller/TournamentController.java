package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.controller.dto.CreateTournamentConfigRequest;
import com.techcup.techcup_futbol.controller.dto.TournamentConfigResponse;
import com.techcup.techcup_futbol.controller.mapper.TournamentMapper;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Torneos", description = "Ciclo de vida del torneo (DRAFT → ACTIVE → IN_PROGRESS → COMPLETED) y configuración de reglas, fechas y canchas")
public class TournamentController {

    private static final Logger log = LoggerFactory.getLogger(TournamentController.class);

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    // ── Torneo

    @Operation(summary = "Crear torneo", description = "Crea un nuevo torneo en estado DRAFT")
    @ApiResponse(responseCode = "201", description = "Torneo creado correctamente")
    @PostMapping
    public ResponseEntity<TournamentResponse> create(
            @Valid @RequestBody CreateTournamentRequest request) {

        log.info("POST /api/tournaments — nombre: {}", request.name());

        Tournament tournament = new Tournament();
        tournament.setName(request.name());
        tournament.setStartDate(request.startDate());
        tournament.setEndDate(request.endDate());
        tournament.setRegistrationFee(request.registrationFee());
        tournament.setMaxTeams(request.maxTeams());
        tournament.setRules(request.rules());

        Tournament created = tournamentService.create(tournament);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TournamentMapper.toResponse(created));
    }

    @Operation(summary = "Listar torneos", description = "Obtiene todos los torneos")
    @ApiResponse(responseCode = "200", description = "Lista de torneos")
    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        log.info("GET /api/tournaments");
        List<TournamentResponse> responses = tournamentService.findAll().stream()
                .map(TournamentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar torneo por ID")
    @ApiResponse(responseCode = "200", description = "Torneo encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(
            @Parameter(description = "ID del torneo") @PathVariable String id) {

        log.info("GET /api/tournaments/{}", id);
        return ResponseEntity.ok(TournamentMapper.toResponse(tournamentService.findById(id)));
    }

    @Operation(summary = "Activar torneo", description = "Cambia el estado a ACTIVE")
    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponse> start(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/start", id);
        return ResponseEntity.ok(TournamentMapper.toResponse(
                tournamentService.updateStatus(id, "ACTIVE")));
    }

    @Operation(summary = "Poner torneo en progreso", description = "Cambia el estado a IN_PROGRESS")
    @PutMapping("/{id}/progress")
    public ResponseEntity<TournamentResponse> progress(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/progress", id);
        return ResponseEntity.ok(TournamentMapper.toResponse(
                tournamentService.updateStatus(id, "IN_PROGRESS")));
    }

    @Operation(summary = "Finalizar torneo", description = "Cambia el estado a COMPLETED")
    @PutMapping("/{id}/finish")
    public ResponseEntity<TournamentResponse> finish(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/finish", id);
        return ResponseEntity.ok(TournamentMapper.toResponse(
                tournamentService.updateStatus(id, "COMPLETED")));
    }

    @Operation(summary = "Eliminar torneo (lógico)", description = "Marca el torneo como DELETED")
    @PutMapping("/{id}/delete")
    public ResponseEntity<TournamentResponse> softDelete(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/delete", id);
        return ResponseEntity.ok(TournamentMapper.toResponse(
                tournamentService.updateStatus(id, "DELETED")));
    }

    // ── Configuración del torneo

    @Operation(summary = "Crear o actualizar configuración del torneo",
               description = "Establece fechas, reglas, horarios y canchas del torneo")
    @ApiResponse(responseCode = "200", description = "Configuración guardada")
    @PutMapping("/{id}/config")
    public ResponseEntity<TournamentConfigResponse> createOrUpdateConfig(
            @PathVariable String id,
            @Valid @RequestBody CreateTournamentConfigRequest request) {

        log.info("PUT /api/tournaments/{}/config", id);

        List<String> importantDates = request.importantDates() == null ? null
                : request.importantDates().stream()
                    .map(d -> d.description() + "|"
                            + (d.date() != null ? d.date().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : ""))
                    .toList();

        List<String> matchSchedules = request.matchSchedules() == null ? null
                : request.matchSchedules().stream()
                    .map(s -> s.dayOfWeek() + "|" + s.startTime() + "|" + s.endTime())
                    .toList();

        List<String> fields = request.fields() == null ? null
                : request.fields().stream()
                    .map(f -> f.name() + "|" + f.location())
                    .toList();

        Tournament updated = tournamentService.createOrUpdateConfig(id,
                request.rules(), request.registrationDeadline(),
                importantDates, matchSchedules, fields, request.sanctions());

        return ResponseEntity.ok(TournamentMapper.toConfigResponse(updated));
    }

    @Operation(summary = "Obtener configuración del torneo")
    @ApiResponse(responseCode = "200", description = "Configuración encontrada")
    @GetMapping("/{id}/config")
    public ResponseEntity<TournamentConfigResponse> findConfig(@PathVariable String id) {
        log.info("GET /api/tournaments/{}/config", id);
        return ResponseEntity.ok(TournamentMapper.toConfigResponse(
                tournamentService.findConfig(id)));
    }
}
