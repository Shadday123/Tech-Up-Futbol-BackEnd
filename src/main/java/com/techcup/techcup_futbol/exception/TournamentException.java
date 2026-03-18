package com.techcup.techcup_futbol.exception;


public class TournamentException extends RuntimeException {

    public static final String REQUEST_NULL =
            "La solicitud del torneo no puede ser nula.";

    public static final String NAME_EMPTY =
            "El nombre del torneo no puede estar vacío.";

    public static final String DATES_NULL =
            "Las fechas de inicio y fin son obligatorias.";
    public static final String END_DATE_NOT_AFTER_START =
            "La fecha de finalización debe ser estrictamente posterior a la de inicio. Inicio: %s | Fin: %s";

    public static final String REGISTRATION_FEE_NEGATIVE =
            "La cuota de inscripción no puede ser negativa. Valor recibido: %.2f";

    public static final String MAX_TEAMS_TOO_LOW =
            "El torneo debe permitir al menos 2 equipos. Valor recibido: %d";

    public static final String INVALID_STATE_NAME =
            "Estado inválido: '%s'. Valores permitidos: %s";
    public static final String INVALID_STATE_TRANSITION =
            "Transición no permitida: %s → %s. Verifique el flujo de estados permitidos.";

    public static final String TOURNAMENT_NOT_FOUND =
            "No se encontró el torneo con ID: %s";


    private final String field;

    public TournamentException(String message) {
        super(message);
        this.field = null;
    }

    public TournamentException(String field, String message) {
        super(message);
        this.field = field;
    }

    public TournamentException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
    }

    public String getField() {
        return field;
    }
}