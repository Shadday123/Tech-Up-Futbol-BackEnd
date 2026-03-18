package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.PaymentDTOs.PaymentResponse;
import com.techcup.techcup_futbol.Controller.dto.PaymentDTOs.UploadReceiptRequest;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.core.model.PaymentStatus;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final Map<String, Payment> payments = new HashMap<>();
    private final Map<String, String> teamPaymentIndex = new HashMap<>();

    @Override
    public PaymentResponse uploadReceipt(UploadReceiptRequest request) {
        log.info("Subiendo comprobante para equipo ID: {}", request.teamId());

        Team team = DataStore.equipos.get(request.teamId());
        if (team == null) {
            throw new PaymentException("teamId",
                    String.format(PaymentException.TEAM_NOT_FOUND, request.teamId()));
        }
        if (request.receiptUrl() == null || request.receiptUrl().isBlank()) {
            throw new PaymentException("receiptUrl", PaymentException.RECEIPT_URL_EMPTY);
        }

        Payment payment = teamPaymentIndex.containsKey(request.teamId())
                ? payments.get(teamPaymentIndex.get(request.teamId()))
                : new Payment();

        if (payment.getId() == null) {
            payment = new Payment(
                    java.util.UUID.randomUUID().toString(),
                    null,
                    team.getPlayers() != null ? team.getPlayers().size() * 50.0 : 0.0,
                    PaymentStatus.PENDING
            );
        }

        if (payment.getCurrentStatus() == PaymentStatus.APPROVED) {
            throw new PaymentException("status", PaymentException.PAYMENT_ALREADY_APPROVED);
        }

        payment.uploadReceipt(request.receiptUrl());
        payments.put(payment.getId(), payment);
        teamPaymentIndex.put(request.teamId(), payment.getId());

        log.info("Comprobante subido — pago ID: {} | estado: {}", payment.getId(), payment.getCurrentStatus());
        return toResponse(payment, team);
    }

    @Override
    public PaymentResponse updateStatus(String paymentId, String newStatus) {
        log.info("Actualizando estado del pago ID: {} → {}", paymentId, newStatus);

        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new PaymentException("id",
                    String.format(PaymentException.PAYMENT_NOT_FOUND, paymentId));
        }

        PaymentStatus next;
        try {
            next = PaymentStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PaymentException("status",
                    String.format(PaymentException.INVALID_STATUS, newStatus));
        }

        validateStatusTransition(payment.getCurrentStatus(), next);
        payment.updateValidationStatus(next);

        log.info("Estado actualizado — pago ID: {} | nuevo estado: {}", paymentId, next);

        Team team = teamPaymentIndex.entrySet().stream()
                .filter(e -> e.getValue().equals(paymentId))
                .map(e -> DataStore.equipos.get(e.getKey()))
                .findFirst().orElse(null);

        return toResponse(payment, team);
    }

    @Override
    public PaymentResponse findById(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new PaymentException("id",
                    String.format(PaymentException.PAYMENT_NOT_FOUND, paymentId));
        }
        Team team = teamPaymentIndex.entrySet().stream()
                .filter(e -> e.getValue().equals(paymentId))
                .map(e -> DataStore.equipos.get(e.getKey()))
                .findFirst().orElse(null);
        return toResponse(payment, team);
    }

    @Override
    public List<PaymentResponse> findAll() {
        List<PaymentResponse> result = new ArrayList<>();
        for (Map.Entry<String, Payment> entry : payments.entrySet()) {
            String teamId = teamPaymentIndex.entrySet().stream()
                    .filter(e -> e.getValue().equals(entry.getKey()))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
            Team team = teamId != null ? DataStore.equipos.get(teamId) : null;
            result.add(toResponse(entry.getValue(), team));
        }
        return result;
    }

    @Override
    public PaymentResponse findByTeamId(String teamId) {
        String paymentId = teamPaymentIndex.get(teamId);
        if (paymentId == null) {
            throw new PaymentException("teamId",
                    String.format(PaymentException.PAYMENT_NOT_FOUND, teamId));
        }
        return findById(paymentId);
    }

    private void validateStatusTransition(PaymentStatus current, PaymentStatus next) {
        boolean allowed = switch (current) {
            case PENDING      -> next == PaymentStatus.UNDER_REVIEW;
            case UNDER_REVIEW -> next == PaymentStatus.APPROVED || next == PaymentStatus.REJECTED;
            case REJECTED     -> next == PaymentStatus.PENDING;
            case APPROVED     -> false;
        };
        if (!allowed) {
            throw new PaymentException("status",
                    String.format(PaymentException.INVALID_TRANSITION, current, next));
        }
    }

    private PaymentResponse toResponse(Payment p, Team team) {
        return new PaymentResponse(
                p.getId(),
                team != null ? team.getId() : null,
                team != null ? team.getTeamName() : null,
                p.getReceiptUrl(),
                p.getAmount(),
                p.getCurrentStatus()
        );
    }
}
