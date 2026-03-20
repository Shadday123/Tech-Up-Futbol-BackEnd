package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.MatchDTOs.*;
import com.techcup.techcup_futbol.core.service.MatchService;
import com.techcup.techcup_futbol.exception.MatchException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody CreateMatchRequest request) {
        log.info("POST /api/matches — local: {} vs visitante: {}", request.localTeamId(), request.visitorTeamId());
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(request));
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<MatchResponse> registerResult(
            @PathVariable String id,
            @Valid @RequestBody RegisterResultRequest request) {
        log.info("PUT /api/matches/{}/result", id);
        return ResponseEntity.ok(matchService.registerResult(id, request));
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> findAll() {
        log.info("GET /api/matches");
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> findById(@PathVariable String id) {
        log.info("GET /api/matches/{}", id);
        return ResponseEntity.ok(matchService.findById(id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MatchResponse>> findByTeam(@PathVariable String teamId) {
        log.info("GET /api/matches/team/{}", teamId);
        return ResponseEntity.ok(matchService.findByTeamId(teamId));
    }

    @ExceptionHandler(MatchException.class)
    public ResponseEntity<String> handleMatchException(MatchException e) {
        log.error("MatchException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
