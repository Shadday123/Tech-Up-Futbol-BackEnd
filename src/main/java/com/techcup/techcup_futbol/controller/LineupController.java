package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.controller.dto.CreateLineupRequest;
import com.techcup.techcup_futbol.controller.dto.LineupResponse;
import com.techcup.techcup_futbol.controller.mapper.LineupMapper;
import com.techcup.techcup_futbol.core.model.Lineup;
import com.techcup.techcup_futbol.core.service.LineupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lineups")
@Tag(name = "Alineaciones", description = "Registro y consulta de formaciones por equipo por partido. Se requieren exactamente 7 titulares por equipo antes de iniciar un encuentro")
public class LineupController {

    private static final Logger log = LoggerFactory.getLogger(LineupController.class);

    private final LineupService lineupService;

    public LineupController(LineupService lineupService) {
        this.lineupService = lineupService;
    }

    @PostMapping
    public ResponseEntity<LineupResponse> create(@Valid @RequestBody CreateLineupRequest request) {
        log.info("POST /api/lineups — partido: {} | equipo: {}", request.matchId(), request.teamId());

        List<String> fieldPositions = request.fieldPositions() == null ? List.of()
                : request.fieldPositions().stream()
                    .map(fp -> fp.playerId() + "|" + fp.x() + "|" + fp.y())
                    .toList();

        Lineup lineup = lineupService.create(request.matchId(), request.teamId(),
                request.formation(), request.starterIds(), request.substituteIds(),
                fieldPositions);

        return ResponseEntity.status(HttpStatus.CREATED).body(LineupMapper.toResponse(lineup));
    }

    @GetMapping("/match/{matchId}/team/{teamId}")
    public ResponseEntity<LineupResponse> findByMatchAndTeam(
            @PathVariable String matchId,
            @PathVariable String teamId) {
        log.info("GET /api/lineups/match/{}/team/{}", matchId, teamId);
        return ResponseEntity.ok(LineupMapper.toResponse(
                lineupService.findByMatchAndTeam(matchId, teamId)));
    }

    @GetMapping("/match/{matchId}/rival")
    public ResponseEntity<LineupResponse> findRivalLineup(
            @PathVariable String matchId,
            @RequestParam String myTeamId) {
        log.info("GET /api/lineups/match/{}/rival — myTeam: {}", matchId, myTeamId);
        return ResponseEntity.ok(LineupMapper.toResponse(
                lineupService.findRivalLineup(matchId, myTeamId)));
    }

}
