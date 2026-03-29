package com.techcup.techcup_futbol.Controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePaymentStatusRequest(
        @NotBlank(message = "El nuevo estado es obligatorio")
        String status
) {}
