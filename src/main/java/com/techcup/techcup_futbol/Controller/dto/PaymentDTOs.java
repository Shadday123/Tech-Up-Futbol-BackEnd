package com.techcup.techcup_futbol.Controller.dto;

import com.techcup.techcup_futbol.core.model.PaymentStatus;
import jakarta.validation.constraints.NotBlank;

public class PaymentDTOs {

    public record UploadReceiptRequest(
            @NotBlank(message = "El ID del equipo es obligatorio")
            String teamId,
            @NotBlank(message = "La URL del comprobante es obligatoria")
            String receiptUrl
    ) {}

    public record UpdatePaymentStatusRequest(
            @NotBlank(message = "El nuevo estado es obligatorio")
            String status
    ) {}

    public record PaymentResponse(
            String id,
            String teamId,
            String teamName,
            String receiptUrl,
            Double amount,
            PaymentStatus currentStatus
    ) {}
}
