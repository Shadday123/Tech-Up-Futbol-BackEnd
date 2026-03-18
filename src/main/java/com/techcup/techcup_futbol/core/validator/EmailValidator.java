package com.techcup.techcup_futbol.core.validator;

import com.techcup.techcup_futbol.exception.PlayerException;

public class EmailValidator {

    private static final String SCHOOL_DOMAIN = "@escuelaing.edu.co";
    private static final String GMAIL_DOMAIN  = "@gmail.com";

    private EmailValidator() {}

    /**
     * Valida formato y dominio del correo.
     * Usa los mensajes centralizados en PlayerException.
     */
    public static void validate(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new PlayerException("email", PlayerException.EMAIL_NULL_OR_BLANK);
        }
        if (!esCorreoValido(correo)) {
            throw new PlayerException("email",
                    String.format(PlayerException.EMAIL_INVALID_DOMAIN, correo));
        }
    }

    /** Retorna true si el correo tiene formato válido y dominio permitido. */
    public static boolean esCorreoValido(String correo) {
        if (correo == null) return false;
        return correo.matches(
                "^[A-Za-z0-9+_.-]+@(escuelaing\\.edu\\.co|gmail\\.com)$"
        );
    }

    /** Retorna true si el correo pertenece al dominio institucional. */
    public static boolean esCorreoInstitucional(String correo) {
        return correo != null && correo.toLowerCase().endsWith(SCHOOL_DOMAIN);
    }
}