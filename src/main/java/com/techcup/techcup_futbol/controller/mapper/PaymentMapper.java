package com.techcup.techcup_futbol.controller.mapper;

import com.techcup.techcup_futbol.controller.dto.PaymentResponse;
import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;

public class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment p, TeamEntity team) {
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
