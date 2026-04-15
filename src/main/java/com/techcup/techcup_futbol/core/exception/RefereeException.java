package com.techcup.techcup_futbol.core.exception;

public class RefereeException extends RuntimeException {

    public static final String REFEREE_NOT_FOUND = "No existe un árbitro con ID: %s";
    public static final String MATCH_NOT_FOUND = "No existe un partido con ID: %s";
    public static final String EMAIL_ALREADY_REGISTERED = "El correo %s ya está registrado. Usa uno diferente.";
    public static final String MATCH_ALREADY_HAS_REFEREE = "El partido ya tiene un árbitro asignado.";

    private final String field;

    public RefereeException(String message) {
        super(message);
        this.field = null;
    }

    public RefereeException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
