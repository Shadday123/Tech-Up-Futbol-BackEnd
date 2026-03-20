package com.techcup.techcup_futbol.exception;

public class PaymentException extends RuntimeException {

    public static final String PAYMENT_NOT_FOUND = "No existe un pago con ID: %s";
    public static final String TEAM_NOT_FOUND = "No existe un equipo con ID: %s";
    public static final String RECEIPT_URL_EMPTY = "La URL del comprobante no puede estar vacía.";
    public static final String INVALID_STATUS = "Estado de pago inválido: '%s'. Valores permitidos: PENDING, UNDER_REVIEW, APPROVED, REJECTED";
    public static final String INVALID_TRANSITION = "Transición de pago no permitida: %s → %s";
    public static final String PAYMENT_ALREADY_APPROVED = "El pago ya está aprobado y no puede modificarse.";

    private final String field;

    public PaymentException(String message) {
        super(message);
        this.field = null;
    }

    public PaymentException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
