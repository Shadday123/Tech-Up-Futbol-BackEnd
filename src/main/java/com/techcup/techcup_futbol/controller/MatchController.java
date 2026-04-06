package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateMatchRequest;
import com.techcup.techcup_futbol.Controller.dto.MatchResponse;
import com.techcup.techcup_futbol.Controller.dto.RegisterResultRequest;
import com.techcup.techcup_futbol.Controller.mapper.MatchMapper;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.MatchEventInput;
import com.techcup.techcup_futbol.core.service.MatchService;
import com.techcup.techcup_futbol.repository.MatchEventRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Partidos", description = "Programación de encuentros entre equipos y registro de resultados. Los resultados actualizan automáticamente la tabla de posiciones")
public class MatchController {

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    private final MatchService matchService;
    private final MatchEventRepository matchEventRepository;

    public MatchController(MatchService matchService, MatchEventRepository matchEventRepository) {
        this.matchService = matchService;
        this.matchEventRepository = matchEventRepository;
    }

    @PostMapping
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody CreateMatchRequest request) {
        log.info("POST /api/matches — local: {} vs visitante: {}", request.localTeamId(), request.visitorTeamId());
        Match match = matchService.create(request.localTeamId(), request.visitorTeamId(),
                request.dateTime(), request.refereeId(), request.field());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MatchMapper.toResponse(match, matchEventRepository.findByMatchId(match.getId())));
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<MatchResponse> registerResult(
            @PathVariable String id,
            @Valid @RequestBody RegisterResultRequest request) {
        log.info("PUT /api/matches/{}/result", id);

        List<MatchEventInput> events = request.events() == null ? null
                : request.events().stream()
                    .map(e -> new MatchEventInput(e.type(), e.minute(), e.playerId()))
                    .toList();

        Match match = matchService.registerResult(id, request.scoreLocal(), request.scoreVisitor(), events);
        return ResponseEntity.ok(MatchMapper.toResponse(match, matchEventRepository.findByMatchId(match.getId())));
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> findAll() {
        log.info("GET /api/matches");
        List<MatchResponse> responses = matchService.findAll().stream()
                .map(m -> MatchMapper.toResponse(m, matchEventRepository.findByMatchId(m.getId())))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> findById(@PathVariable String id) {
        log.info("GET /api/matches/{}", id);
        Match match = matchService.findById(id);
        return ResponseEntity.ok(MatchMapper.toResponse(match, matchEventRepository.findByMatchId(match.getId())));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MatchResponse>> findByTeam(@PathVariable String teamId) {
        log.info("GET /api/matches/team/{}", teamId);
        List<MatchResponse> responses = matchService.findByTeamId(teamId).stream()
                .map(m -> MatchMapper.toResponse(m, matchEventRepository.findByMatchId(m.getId())))
                .toList();
        return ResponseEntity.ok(responses);
    }

}
