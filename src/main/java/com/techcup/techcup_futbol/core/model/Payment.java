package com.techcup.techcup_futbol.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private String id;

    private String receiptUrl;

    private Double amount;

    private PaymentStatus currentStatus = PaymentStatus.PENDING;

    public void uploadReceipt(String url) {
        this.receiptUrl = url;
        this.currentStatus = PaymentStatus.UNDER_REVIEW;
    }

    public void updateValidationStatus(PaymentStatus status) {
        this.currentStatus = status;
    }
}
