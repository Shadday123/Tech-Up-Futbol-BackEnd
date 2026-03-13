package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.service.TournamentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private static final Logger log = LoggerFactory.getLogger(TournamentController.class);

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        log.info("POST /api/tournaments — creando torneo: {}", request.name());
        TournamentResponse newTournament = tournamentService.create(request);
        log.info("Torneo creado con id: {}", newTournament.id());
        return new ResponseEntity<>(newTournament, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        log.info("GET /api/tournaments — listando todos los torneos");
        List<TournamentResponse> torneos = tournamentService.findAll();
        log.info("Torneos encontrados: {}", torneos.size());
        return ResponseEntity.ok(torneos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(@PathVariable String id) {
        log.info("GET /api/tournaments/{} — buscando torneo", id);
        TournamentResponse torneo = tournamentService.findById(id);
        return ResponseEntity.ok(torneo);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponse> start(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/start — iniciando torneo", id);
        TournamentResponse torneo = tournamentService.updateStatus(id, "ACTIVE");
        log.info("Torneo id: {} actualizado a estado: {}", id, torneo.currentState());
        return ResponseEntity.ok(torneo);
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<TournamentResponse> finish(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/finish — finalizando torneo", id);
        TournamentResponse torneo = tournamentService.updateStatus(id, "COMPLETED");
        log.info("Torneo id: {} actualizado a estado: {}", id, torneo.currentState());
        return ResponseEntity.ok(torneo);
    }
}