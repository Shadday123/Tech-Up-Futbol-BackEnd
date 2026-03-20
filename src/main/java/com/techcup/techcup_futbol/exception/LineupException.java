package com.techcup.techcup_futbol.exception;

public class LineupException extends RuntimeException {

    public static final String LINEUP_NOT_FOUND = "No existe alineación para el partido '%s' del equipo '%s'.";
    public static final String MATCH_NOT_FOUND = "No existe un partido con ID: %s";
    public static final String TEAM_NOT_FOUND = "No existe equipo con ID: %s";
    public static final String PLAYER_NOT_IN_TEAM = "El jugador '%s' no pertenece al equipo '%s'.";
    public static final String WRONG_STARTERS_COUNT = "Debe seleccionar exactamente 7 titulares. Recibidos: %d";
    public static final String LINEUP_ALREADY_EXISTS = "Ya existe una alineación para el partido '%s' del equipo '%s'.";
    public static final String RIVAL_LINEUP_NOT_PUBLISHED = "La alineación del equipo rival aún no ha sido publicada.";

    private final String field;

    public LineupException(String message) {
        super(message);
        this.field = null;
    }

    public LineupException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
