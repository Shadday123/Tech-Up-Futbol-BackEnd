package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.controller.dto.BracketResponse;
import com.techcup.techcup_futbol.controller.dto.GenerateBracketRequest;
import com.techcup.techcup_futbol.controller.mapper.BracketMapper;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentBrackets;
import com.techcup.techcup_futbol.core.service.BracketService;
import com.techcup.techcup_futbol.core.service.TournamentService;
import com.techcup.techcup_futbol.persistence.entity.TournamentBracketsEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brackets")
@Tag(name = "Llaves Eliminatorias", description = "Generación automática de brackets por torneo (requiere número de equipos potencia de 2) y avance de ganadores por ronda")
public class BracketController {

    private static final Logger log = LoggerFactory.getLogger(BracketController.class);

    private final BracketService bracketService;
    private final TournamentService tournamentService;

    public BracketController(BracketService bracketService, TournamentService tournamentService) {
        this.bracketService = bracketService;
        this.tournamentService = tournamentService;
    }

    @PostMapping("/tournament/{tournamentId}/generate")
    public ResponseEntity<BracketResponse> generate(
            @PathVariable String tournamentId,
            @Valid @RequestBody GenerateBracketRequest request) {
        log.info("POST /api/brackets/tournament/{}/generate — equipos: {}", tournamentId, request.teamsCount());

        List<TournamentBracketsEntity> phases = bracketService.generate(tournamentId, request.teamsCount());
        Tournament tournament = tournamentService.findById(tournamentId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BracketMapper.toResponse(tournamentId, tournament, phases));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<BracketResponse> findByTournament(@PathVariable String tournamentId) {
        log.info("GET /api/brackets/tournament/{}", tournamentId);

        List<TournamentBrackets> phases = bracketService.findByTournamentId(tournamentId);
        Tournament tournament = tournamentService.findById(tournamentId);

        return ResponseEntity.ok(BracketMapper.toResponseFromModels(tournamentId, tournament, phases));
    }

    @PutMapping("/tournament/{tournamentId}/match/{matchId}/advance")
    public ResponseEntity<BracketResponse> advanceWinner(
            @PathVariable String tournamentId,
            @PathVariable String matchId) {
        log.info("PUT /api/brackets/tournament/{}/match/{}/advance", tournamentId, matchId);

        List<TournamentBrackets> phases = bracketService.advanceWinner(tournamentId, matchId);
        Tournament tournament = tournamentService.findById(tournamentId);

        return ResponseEntity.ok(BracketMapper.toResponseFromModels(tournamentId, tournament, phases));
    }
}
