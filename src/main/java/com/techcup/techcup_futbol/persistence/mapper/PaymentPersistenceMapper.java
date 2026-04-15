package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.persistence.entity.PaymentEntity;

public class PaymentPersistenceMapper {

    private PaymentPersistenceMapper() {}

    public static PaymentEntity toEntity(Payment payment) {
        if (payment == null) return null;

        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId());
        entity.setTeamId(payment.getTeamId());
        entity.setReceiptUrl(payment.getReceiptUrl());
        entity.setAmount(payment.getAmount());
        entity.setCurrentStatus(payment.getCurrentStatus());

        return entity;
    }

    public static Payment toDomain(PaymentEntity entity) {
        if (entity == null) return null;

        Payment payment = new Payment();
        payment.setId(entity.getId());
        payment.setTeamId(entity.getTeamId());
        payment.setReceiptUrl(entity.getReceiptUrl());
        payment.setAmount(entity.getAmount());
        payment.setCurrentStatus(entity.getCurrentStatus());

        return payment;
    }
}