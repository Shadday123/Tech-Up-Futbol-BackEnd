package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.LineupDTOs.*;
import com.techcup.techcup_futbol.core.service.LineupService;
import com.techcup.techcup_futbol.exception.LineupException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lineups")
public class LineupController {

    private static final Logger log = LoggerFactory.getLogger(LineupController.class);

    private final LineupService lineupService;

    public LineupController(LineupService lineupService) {
        this.lineupService = lineupService;
    }

    @PostMapping
    public ResponseEntity<LineupResponse> create(@Valid @RequestBody CreateLineupRequest request) {
        log.info("POST /api/lineups — partido: {} | equipo: {}", request.matchId(), request.teamId());
        return ResponseEntity.status(HttpStatus.CREATED).body(lineupService.create(request));
    }

    @GetMapping("/match/{matchId}/team/{teamId}")
    public ResponseEntity<LineupResponse> findByMatchAndTeam(
            @PathVariable String matchId,
            @PathVariable String teamId) {
        log.info("GET /api/lineups/match/{}/team/{}", matchId, teamId);
        return ResponseEntity.ok(lineupService.findByMatchAndTeam(matchId, teamId));
    }

    @GetMapping("/match/{matchId}/rival")
    public ResponseEntity<LineupResponse> findRivalLineup(
            @PathVariable String matchId,
            @RequestParam String myTeamId) {
        log.info("GET /api/lineups/match/{}/rival — myTeam: {}", matchId, myTeamId);
        return ResponseEntity.ok(lineupService.findRivalLineup(matchId, myTeamId));
    }

    @ExceptionHandler(LineupException.class)
    public ResponseEntity<String> handleLineupException(LineupException e) {
        log.error("LineupException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
