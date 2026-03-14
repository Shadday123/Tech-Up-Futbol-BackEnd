package com.techcup.techcup_futbol.Controller;


import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        TournamentResponse newTournament = tournamentService.create(request);
        return new ResponseEntity<>(newTournament, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        return ResponseEntity.ok(tournamentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(tournamentService.findById(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponse> start(@PathVariable String id) {
        return ResponseEntity.ok(tournamentService.updateStatus(id, "ACTIVE"));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<TournamentResponse> finish(@PathVariable String id) {
        return ResponseEntity.ok(tournamentService.updateStatus(id, "COMPLETED"));
    }
}