package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.service.TournamentService;
import com.techcup.techcup_futbol.exception.TournamentException;

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

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Tournaments", description = "API para la gestión de torneos")
public class TournamentController {

    private static final Logger log = LoggerFactory.getLogger(TournamentController.class);

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Operation(summary = "Crear torneo", description = "Crea un nuevo torneo")
    @ApiResponse(responseCode = "201", description = "Torneo creado correctamente")
    @PostMapping
    public ResponseEntity<TournamentResponse> create(
            @Valid @RequestBody CreateTournamentRequest request) {

        log.info("POST /api/tournaments — nombre: {}", request.name());
        TournamentResponse created = tournamentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Listar torneos", description = "Obtiene todos los torneos")
    @ApiResponse(responseCode = "200", description = "Lista de torneos")
    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        log.info("GET /api/tournaments");
        return ResponseEntity.ok(tournamentService.findAll());
    }

    @Operation(summary = "Buscar torneo por ID", description = "Obtiene un torneo por su ID")
    @ApiResponse(responseCode = "200", description = "Torneo encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(
            @Parameter(description = "ID del torneo") @PathVariable String id) {

        log.info("GET /api/tournaments/{}", id);
        return ResponseEntity.ok(tournamentService.findById(id));
    }

    @Operation(summary = "Iniciar torneo", description = "Cambia el estado del torneo a ACTIVO")
    @ApiResponse(responseCode = "200", description = "Torneo iniciado")
    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponse> start(
            @Parameter(description = "ID del torneo") @PathVariable String id) {

        log.info("PUT /api/tournaments/{}/start", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "ACTIVE"));
    }

    @Operation(summary = "Poner torneo en progreso", description = "Cambia el estado del torneo a EN PROGRESO")
    @ApiResponse(responseCode = "200", description = "Torneo en progreso")
    @PutMapping("/{id}/progress")
    public ResponseEntity<TournamentResponse> progress(
            @Parameter(description = "ID del torneo") @PathVariable String id) {

        log.info("PUT /api/tournaments/{}/progress", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "IN_PROGRESS"));
    }

    @Operation(summary = "Finalizar torneo", description = "Cambia el estado del torneo a COMPLETADO")
    @ApiResponse(responseCode = "200", description = "Torneo finalizado")
    @PutMapping("/{id}/finish")
    public ResponseEntity<TournamentResponse> finish(
            @Parameter(description = "ID del torneo") @PathVariable String id) {

        log.info("PUT /api/tournaments/{}/finish", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "COMPLETED"));
    }

    @Operation(summary = "Eliminar torneo (lógico)", description = "Marca el torneo como eliminado")
    @ApiResponse(responseCode = "200", description = "Torneo eliminado lógicamente")
    @PutMapping("/{id}/delete")
    public ResponseEntity<TournamentResponse> softDelete(
            @Parameter(description = "ID del torneo") @PathVariable String id) {

        log.info("PUT /api/tournaments/{}/delete", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "DELETED"));
    }

    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<String> handleTournamentException(TournamentException e) {
        log.error("TournamentException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}