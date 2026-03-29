package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UploadReceiptRequest(
        @NotBlank(message = "El ID del equipo es obligatorio")
        String teamId,
        @NotBlank(message = "La URL del comprobante es obligatoria")
        String receiptUrl
) {}
