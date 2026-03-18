package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.*;
import com.techcup.techcup_futbol.core.service.TournamentConfigService;
import com.techcup.techcup_futbol.exception.TournamentException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}/config")
public class TournamentConfigController {

    private static final Logger log = LoggerFactory.getLogger(TournamentConfigController.class);

    private final TournamentConfigService configService;

    public TournamentConfigController(TournamentConfigService configService) {
        this.configService = configService;
    }

    @PutMapping
    public ResponseEntity<TournamentConfigResponse> createOrUpdate(
            @PathVariable String tournamentId,
            @Valid @RequestBody CreateTournamentConfigRequest request) {
        log.info("PUT /api/tournaments/{}/config", tournamentId);
        return ResponseEntity.ok(configService.createOrUpdate(tournamentId, request));
    }

    @GetMapping
    public ResponseEntity<TournamentConfigResponse> findByTournament(
            @PathVariable String tournamentId) {
        log.info("GET /api/tournaments/{}/config", tournamentId);
        return ResponseEntity.ok(configService.findByTournamentId(tournamentId));
    }

    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<String> handleTournamentException(TournamentException e) {
        log.error("TournamentException — {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
