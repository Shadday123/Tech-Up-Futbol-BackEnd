package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.core.model.PaymentStatus;
import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.persistence.entity.PaymentEntity;
import com.techcup.techcup_futbol.persistence.mapper.PaymentPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PaymentRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final TeamRepository teamRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, TeamRepository teamRepository) {
        this.paymentRepository = paymentRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public Payment uploadReceipt(String teamId, String receiptUrl) {
        log.info("Subiendo comprobante para equipo ID: {}", teamId);

        teamRepository.findById(teamId)
                .orElseThrow(() -> new PaymentException("teamId",
                        String.format(PaymentException.TEAM_NOT_FOUND, teamId)));

        if (receiptUrl == null || receiptUrl.isBlank()) {
            throw new PaymentException("receiptUrl", PaymentException.RECEIPT_URL_EMPTY);
        }

        PaymentEntity paymentEntity = paymentRepository.findByTeamId(teamId)
                .orElseGet(() -> {
                    PaymentEntity p = new PaymentEntity();
                    p.setId(IdGenerator.generateId());
                    p.setTeamId(teamId);
                    p.setAmount(50.0 * 11); // 11 jugadores fijos por equipo
                    p.setCurrentStatus(PaymentStatus.PENDING);
                    return p;
                });

        if (paymentEntity.getCurrentStatus() == PaymentStatus.APPROVED) {
            throw new PaymentException("status", PaymentException.PAYMENT_ALREADY_APPROVED);
        }

        paymentEntity.setReceiptUrl(receiptUrl);
        PaymentEntity saved = paymentRepository.save(paymentEntity);

        log.info("Comprobante subido — pago ID: {} | estado: {}",
                saved.getId(), saved.getCurrentStatus());
        return PaymentPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Payment updateStatus(String paymentId, String newStatus) {
        log.info("Actualizando estado del pago ID: {} → {}", paymentId, newStatus);

        PaymentEntity payment = paymentRepository.findById(paymentId)
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
        payment.setCurrentStatus(next);
        PaymentEntity saved = paymentRepository.save(payment);

        log.info("Estado actualizado — pago ID: {} | nuevo estado: {}", paymentId, next);
        return PaymentPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment findById(String paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("id",
                        String.format(PaymentException.PAYMENT_NOT_FOUND, paymentId)));
        return PaymentPersistenceMapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll().stream()
                .map(PaymentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Payment findByTeamId(String teamId) {
        PaymentEntity entity = paymentRepository.findByTeamId(teamId)
                .orElseThrow(() -> new PaymentException("teamId",
                        String.format(PaymentException.PAYMENT_NOT_FOUND, teamId)));
        return PaymentPersistenceMapper.toDomain(entity);
    }

    private void validateStatusTransition(PaymentStatus current, PaymentStatus next) {
        boolean allowed = switch (current) {
            case PENDING -> next == PaymentStatus.UNDER_REVIEW;
            case UNDER_REVIEW -> next == PaymentStatus.APPROVED || next == PaymentStatus.REJECTED;
            case REJECTED -> next == PaymentStatus.PENDING;
            case APPROVED -> false;
        };
        if (!allowed) {
            throw new PaymentException("status",
                    String.format(PaymentException.INVALID_TRANSITION, current, next));
        }
    }
}
