package com.techcup.techcup_futbol.Controller;


import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.service.TournamentService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Torneos", description = "ENDPOINT para la gestión de torneos")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    @Operation(summary = "Creacion de torneo", description = "Crea el toreno correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Error, datos invalidos para torneo")
    })
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        TournamentResponse newTournament = tournamentService.create(request);
        return new ResponseEntity<>(newTournament, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtiene la lista de los torneos creados", description = "Lista los torneos para jugar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneos listados")
    })

    public ResponseEntity<List<TournamentResponse>> findAll() {
        return ResponseEntity.ok(tournamentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Encontrar torneo", description = "Busca un torneo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo no encontrado"),
            @ApiResponse(responseCode = "404", description = "Torneo inexistente")
    })
    public ResponseEntity<TournamentResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(tournamentService.findById(id));
    }
    @PutMapping("/{id}/start")
    @Operation(summary = "Iniciar torneo", description = "Cambia el estado del torneo a ACTIVO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo iniciado correctamente"),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    public ResponseEntity<TournamentResponse> start(@PathVariable String id) {
        return ResponseEntity.ok(tournamentService.updateStatus(id, "ACTIVE"));
    }

    @PutMapping("/{id}/finish")
    @Operation(summary = "Finalizar torneo", description = "Cambia el estado del torneo a COMPLETADO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo finalizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    public ResponseEntity<TournamentResponse> finish(@PathVariable String id) {
        return ResponseEntity.ok(tournamentService.updateStatus(id, "COMPLETED"));
    }
}