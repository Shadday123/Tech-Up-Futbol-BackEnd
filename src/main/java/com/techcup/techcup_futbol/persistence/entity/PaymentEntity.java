package com.techcup.techcup_futbol.persistence.entity;


import jakarta.persistence.*;
import com.techcup.techcup_futbol.core.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class PaymentEntity {

    @Id
    private String id;

    @Column(name = "team_id")
    private String teamId;

    private String receiptUrl;

    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus currentStatus = PaymentStatus.PENDING;

    public void uploadReceipt(String url) {
        this.receiptUrl = url;
        this.currentStatus = PaymentStatus.UNDER_REVIEW;
    }

    public void updateValidationStatus(PaymentStatus status) {
        this.currentStatus = status;
    }
}