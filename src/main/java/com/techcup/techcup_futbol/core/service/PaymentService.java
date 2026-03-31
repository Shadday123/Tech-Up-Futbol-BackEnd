package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment uploadReceipt(String teamId, String receiptUrl);
    Payment updateStatus(String paymentId, String newStatus);
    Payment findById(String paymentId);
    List<Payment> findAll();
    Payment findByTeamId(String teamId);
}
