package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Torneos", description = "Gestión de torneos de fútbol")
public class TournamentController {

    private static final Logger log = LoggerFactory.getLogger(TournamentController.class);

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Operation(summary = "Crear un torneo", description = "Registra un nuevo torneo en estado DRAFT")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Torneo creado exitosamente",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos del torneo inválidos",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        log.info("POST /api/tournaments — creando torneo: {}", request.name());
        TournamentResponse newTournament = tournamentService.create(request);
        log.info("Torneo creado con id: {}", newTournament.id());
        return new ResponseEntity<>(newTournament, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los torneos", description = "Retorna todos los torneos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de torneos obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = TournamentResponse.class)))
    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        log.info("GET /api/tournaments — listando todos los torneos");
        List<TournamentResponse> torneos = tournamentService.findAll();
        log.info("Torneos encontrados: {}", torneos.size());
        return ResponseEntity.ok(torneos);
    }

    @Operation(summary = "Buscar torneo por ID", description = "Retorna un torneo específico por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo encontrado",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(
            @Parameter(description = "ID del torneo", example = "T001") @PathVariable String id) {
        log.info("GET /api/tournaments/{} — buscando torneo", id);
        TournamentResponse torneo = tournamentService.findById(id);
        return ResponseEntity.ok(torneo);
    }

    @Operation(summary = "Iniciar torneo", description = "Cambia el estado del torneo a ACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo iniciado exitosamente",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Transición de estado inválida",
                    content = @Content)
    })
    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponse> start(
            @Parameter(description = "ID del torneo", example = "T001") @PathVariable String id) {
        log.info("PUT /api/tournaments/{}/start — iniciando torneo", id);
        TournamentResponse torneo = tournamentService.updateStatus(id, "ACTIVE");
        log.info("Torneo id: {} actualizado a estado: {}", id, torneo.currentState());
        return ResponseEntity.ok(torneo);
    }

    @Operation(summary = "Finalizar torneo", description = "Cambia el estado del torneo a COMPLETED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo finalizado exitosamente",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Transición de estado inválida",
                    content = @Content)
    })
    @PutMapping("/{id}/finish")
    public ResponseEntity<TournamentResponse> finish(
            @Parameter(description = "ID del torneo", example = "T001") @PathVariable String id) {
        log.info("PUT /api/tournaments/{}/finish — finalizando torneo", id);
        TournamentResponse torneo = tournamentService.updateStatus(id, "COMPLETED");
        log.info("Torneo id: {} actualizado a estado: {}", id, torneo.currentState());
        return ResponseEntity.ok(torneo);
    }
}