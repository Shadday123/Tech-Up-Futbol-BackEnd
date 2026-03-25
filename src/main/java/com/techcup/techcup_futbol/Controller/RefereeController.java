package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.RefereeDTOs.*;
import com.techcup.techcup_futbol.core.service.RefereeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referees")
@Tag(name = "Árbitros", description = "Registro de árbitros del torneo y asignación a partidos específicos")
public class RefereeController {

    private static final Logger log = LoggerFactory.getLogger(RefereeController.class);

    private final RefereeService refereeService;

    public RefereeController(RefereeService refereeService) {
        this.refereeService = refereeService;
    }

    @PostMapping
    public ResponseEntity<RefereeResponse> create(@Valid @RequestBody CreateRefereeRequest request) {
        log.info("POST /api/referees — nombre: {}", request.fullname());
        return ResponseEntity.status(HttpStatus.CREATED).body(refereeService.create(request));
    }

    @PostMapping("/match/{matchId}/assign")
    public ResponseEntity<RefereeResponse> assignToMatch(
            @PathVariable String matchId,
            @Valid @RequestBody AssignRefereeRequest request) {
        log.info("POST /api/referees/match/{}/assign — árbitro: {}", matchId, request.refereeId());
        return ResponseEntity.ok(refereeService.assignToMatch(matchId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefereeResponse> findById(@PathVariable String id) {
        log.info("GET /api/referees/{}", id);
        return ResponseEntity.ok(refereeService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RefereeResponse>> findAll() {
        log.info("GET /api/referees");
        return ResponseEntity.ok(refereeService.findAll());
    }


}
