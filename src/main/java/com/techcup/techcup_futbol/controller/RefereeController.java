package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.controller.dto.AssignRefereeRequest;
import com.techcup.techcup_futbol.controller.dto.CreateRefereeRequest;
import com.techcup.techcup_futbol.controller.dto.RefereeRegistrationRequest;
import com.techcup.techcup_futbol.controller.dto.RefereeResponse;
import com.techcup.techcup_futbol.controller.mapper.RefereeMapper;
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

    @PostMapping("/registro")
    public ResponseEntity<RefereeResponse> registrar(@Valid @RequestBody RefereeRegistrationRequest request) {
        log.info("POST /api/referees/registro — email: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RefereeMapper.toResponse(
                        refereeService.registrar(
                                request.fullname(),
                                request.email(),
                                request.password(),
                                request.license(),
                                request.experience())));
    }

    @PostMapping
    public ResponseEntity<RefereeResponse> create(@Valid @RequestBody CreateRefereeRequest request) {
        log.info("POST /api/referees — nombre: {}", request.fullname());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RefereeMapper.toResponse(
                        refereeService.create(request.fullname(), request.email())));
    }

    @PostMapping("/match/{matchId}/assign")
    public ResponseEntity<RefereeResponse> assignToMatch(
            @PathVariable String matchId,
            @Valid @RequestBody AssignRefereeRequest request) {
        log.info("POST /api/referees/match/{}/assign — árbitro: {}", matchId, request.refereeId());
        return ResponseEntity.ok(RefereeMapper.toResponse(
                refereeService.assignToMatch(matchId, request.refereeId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefereeResponse> findById(@PathVariable String id) {
        log.info("GET /api/referees/{}", id);
        return ResponseEntity.ok(RefereeMapper.toResponse(refereeService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<RefereeResponse>> findAll() {
        log.info("GET /api/referees");
        List<RefereeResponse> responses = refereeService.findAll().stream()
                .map(RefereeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

}
