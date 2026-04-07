package com.techcup.techcup_futbol.controller.dto;

import com.techcup.techcup_futbol.core.model.PaymentStatus;

public record PaymentResponse(
        String id,
        String teamId,
        String teamName,
        String receiptUrl,
        Double amount,
        PaymentStatus currentStatus
) {}
