package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.core.model.PaymentStatus;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.persistence.repository.PaymentRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Override
    @Transactional
    public Payment uploadReceipt(String teamId, String receiptUrl) {
        log.info("Subiendo comprobante para equipo ID: {}", teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new PaymentException("teamId",
                        String.format(PaymentException.TEAM_NOT_FOUND, teamId)));

        if (receiptUrl == null || receiptUrl.isBlank()) {
            throw new PaymentException("receiptUrl", PaymentException.RECEIPT_URL_EMPTY);
        }

        Payment payment = paymentRepository.findByTeamId(teamId)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setId(IdGenerator.generateId());
                    p.setTeamId(teamId);
                    p.setAmount(team.getPlayers() != null ? team.getPlayers().size() * 50.0 : 0.0);
                    p.setCurrentStatus(PaymentStatus.PENDING);
                    return p;
                });

        if (payment.getCurrentStatus() == PaymentStatus.APPROVED) {
            throw new PaymentException("status", PaymentException.PAYMENT_ALREADY_APPROVED);
        }

        payment.uploadReceipt(receiptUrl);
        paymentRepository.save(payment);

        log.info("Comprobante subido — pago ID: {} | estado: {}", payment.getId(), payment.getCurrentStatus());
        return payment;
    }

    @Override
    @Transactional
    public Payment updateStatus(String paymentId, String newStatus) {
        log.info("Actualizando estado del pago ID: {} → {}", paymentId, newStatus);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("id",
                        String.format(PaymentException.PAYMENT_NOT_FOUND, paymentId)));

        PaymentStatus next;
        try {
            next = PaymentStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PaymentException("status",
                    String.format(PaymentException.INVALID_STATUS, newStatus));
        }

        validateStatusTransition(payment.getCurrentStatus(), next);
        payment.updateValidationStatus(next);
        paymentRepository.save(payment);

        log.info("Estado actualizado — pago ID: {} | nuevo estado: {}", paymentId, next);
        return payment;
    }

    @Override
    public Payment findById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("id",
                        String.format(PaymentException.PAYMENT_NOT_FOUND, paymentId)));
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment findByTeamId(String teamId) {
        return paymentRepository.findByTeamId(teamId)
                .orElseThrow(() -> new PaymentException("teamId",
                        String.format(PaymentException.PAYMENT_NOT_FOUND, teamId)));
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
}
