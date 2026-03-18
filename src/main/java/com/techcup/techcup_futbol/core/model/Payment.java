package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String receiptUrl;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus currentStatus = PaymentStatus.PENDING; // Estado inicial


    public void uploadReceipt(String url) {
        this.receiptUrl = url;
        this.currentStatus = PaymentStatus.UNDER_REVIEW; // Pasa a revisión al subirlo
    }

    public void updateValidationStatus(PaymentStatus status) {
        this.currentStatus = status; // Acción del Organizador
    }
}
