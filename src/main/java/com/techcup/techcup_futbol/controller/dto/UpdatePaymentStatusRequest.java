package com.techcup.techcup_futbol.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePaymentStatusRequest(
        @NotBlank(message = "El nuevo estado es obligatorio")
        String status
) {}
