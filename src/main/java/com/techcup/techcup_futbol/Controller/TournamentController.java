package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.service.TournamentService;
import com.techcup.techcup_futbol.exception.TournamentException;
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
        log.info("POST /api/tournaments — nombre: {}", request.name());
        TournamentResponse created = tournamentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        log.info("GET /api/tournaments");
        return ResponseEntity.ok(tournamentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(@PathVariable String id) {
        log.info("GET /api/tournaments/{}", id);
        return ResponseEntity.ok(tournamentService.findById(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponse> start(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/start", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "ACTIVE"));
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<TournamentResponse> progress(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/progress", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "IN_PROGRESS"));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<TournamentResponse> finish(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/finish", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "COMPLETED"));
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<TournamentResponse> softDelete(@PathVariable String id) {
        log.info("PUT /api/tournaments/{}/delete", id);
        return ResponseEntity.ok(tournamentService.updateStatus(id, "DELETED"));
    }

    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<String> handleTournamentException(TournamentException e) {
        log.error("TournamentException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}