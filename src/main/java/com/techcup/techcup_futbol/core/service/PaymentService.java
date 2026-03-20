package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.PaymentDTOs.PaymentResponse;
import com.techcup.techcup_futbol.Controller.dto.PaymentDTOs.UploadReceiptRequest;

import java.util.List;

public interface PaymentService {
    PaymentResponse uploadReceipt(UploadReceiptRequest request);
    PaymentResponse updateStatus(String paymentId, String newStatus);
    PaymentResponse findById(String paymentId);
    List<PaymentResponse> findAll();
    PaymentResponse findByTeamId(String teamId);
}
