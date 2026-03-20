package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.BracketDTOs.*;
import com.techcup.techcup_futbol.core.service.BracketService;
import com.techcup.techcup_futbol.exception.BracketException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brackets")
public class BracketController {

    private static final Logger log = LoggerFactory.getLogger(BracketController.class);

    private final BracketService bracketService;

    public BracketController(BracketService bracketService) {
        this.bracketService = bracketService;
    }

    @PostMapping("/tournament/{tournamentId}/generate")
    public ResponseEntity<BracketResponse> generate(
            @PathVariable String tournamentId,
            @Valid @RequestBody GenerateBracketRequest request) {
        log.info("POST /api/brackets/tournament/{}/generate — equipos: {}",
                tournamentId, request.teamsCount());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bracketService.generate(tournamentId, request));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<BracketResponse> findByTournament(@PathVariable String tournamentId) {
        log.info("GET /api/brackets/tournament/{}", tournamentId);
        return ResponseEntity.ok(bracketService.findByTournamentId(tournamentId));
    }

    @PutMapping("/tournament/{tournamentId}/match/{matchId}/advance")
    public ResponseEntity<BracketResponse> advanceWinner(
            @PathVariable String tournamentId,
            @PathVariable String matchId) {
        log.info("PUT /api/brackets/tournament/{}/match/{}/advance", tournamentId, matchId);
        return ResponseEntity.ok(bracketService.advanceWinner(tournamentId, matchId));
    }

    @ExceptionHandler(BracketException.class)
    public ResponseEntity<String> handleBracketException(BracketException e) {
        log.error("BracketException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
