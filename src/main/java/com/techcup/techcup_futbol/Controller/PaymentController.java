package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.PaymentDTOs.*;
import com.techcup.techcup_futbol.core.service.PaymentService;
import com.techcup.techcup_futbol.exception.PaymentException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<PaymentResponse> uploadReceipt(
            @Valid @RequestBody UploadReceiptRequest request) {
        log.info("POST /api/payments/upload — equipo: {}", request.teamId());
        PaymentResponse response = paymentService.uploadReceipt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        log.info("PUT /api/payments/{}/status — nuevo estado: {}", id, request.status());
        return ResponseEntity.ok(paymentService.updateStatus(id, request.status()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable String id) {
        log.info("GET /api/payments/{}", id);
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll() {
        log.info("GET /api/payments");
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<PaymentResponse> findByTeam(@PathVariable String teamId) {
        log.info("GET /api/payments/team/{}", teamId);
        return ResponseEntity.ok(paymentService.findByTeamId(teamId));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<String> handlePaymentException(PaymentException e) {
        log.error("PaymentException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
