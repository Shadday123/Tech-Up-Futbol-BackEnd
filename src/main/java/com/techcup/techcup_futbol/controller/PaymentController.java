package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.controller.dto.PaymentResponse;
import com.techcup.techcup_futbol.controller.dto.UpdatePaymentStatusRequest;
import com.techcup.techcup_futbol.controller.dto.UploadReceiptRequest;
import com.techcup.techcup_futbol.controller.mapper.PaymentMapper;
import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.persistence.entity.*;
import com.techcup.techcup_futbol.core.service.PaymentService;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Pagos", description = "Gestión de comprobantes de inscripción al torneo. Flujo: PENDING → UNDER_REVIEW → APPROVED / REJECTED. Un pago rechazado puede volver a PENDING")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final TeamRepository teamRepository;

    public PaymentController(PaymentService paymentService, TeamRepository teamRepository) {
        this.paymentService = paymentService;
        this.teamRepository = teamRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<PaymentResponse> uploadReceipt(
            @Valid @RequestBody UploadReceiptRequest request) {
        log.info("POST /api/payments/upload — equipo: {}", request.teamId());
        Payment payment = paymentService.uploadReceipt(request.teamId(), request.receiptUrl());
        TeamEntity team = teamRepository.findById(payment.getTeamId()).orElse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentMapper.toResponse(payment, team));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        log.info("PUT /api/payments/{}/status — nuevo estado: {}", id, request.status());
        Payment payment = paymentService.updateStatus(id, request.status());
        TeamEntity team = payment.getTeamId() != null
                ? teamRepository.findById(payment.getTeamId()).orElse(null) : null;
        return ResponseEntity.ok(PaymentMapper.toResponse(payment, team));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable String id) {
        log.info("GET /api/payments/{}", id);
        Payment payment = paymentService.findById(id);
        TeamEntity team = payment.getTeamId() != null
                ? teamRepository.findById(payment.getTeamId()).orElse(null) : null;
        return ResponseEntity.ok(PaymentMapper.toResponse(payment, team));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll() {
        log.info("GET /api/payments");
        List<PaymentResponse> responses = paymentService.findAll().stream()
                .map(p -> {
                    TeamEntity team = p.getTeamId() != null
                            ? teamRepository.findById(p.getTeamId()).orElse(null) : null;
                    return PaymentMapper.toResponse(p, team);
                }).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<PaymentResponse> findByTeam(@PathVariable String teamId) {
        log.info("GET /api/payments/team/{}", teamId);
        Payment payment = paymentService.findByTeamId(teamId);
        TeamEntity team = teamRepository.findById(teamId).orElse(null);
        return ResponseEntity.ok(PaymentMapper.toResponse(payment, team));
    }

}
