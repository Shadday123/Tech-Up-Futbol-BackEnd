package com.techcup.techcup_futbol.Controller.mapper;

import com.techcup.techcup_futbol.Controller.dto.PaymentResponse;
import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.core.model.Team;

public class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment p, Team team) {
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
