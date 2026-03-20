package com.techcup.techcup_futbol.exception;

public class MatchException extends RuntimeException {

    public static final String MATCH_NOT_FOUND = "No existe un partido con ID: %s";
    public static final String TEAM_NOT_FOUND = "No existe equipo con ID: %s";
    public static final String SAME_TEAM = "El equipo local y visitante no pueden ser el mismo.";
    public static final String RESULT_ALREADY_REGISTERED = "El resultado de este partido ya fue registrado.";
    public static final String GOALS_MISMATCH = "La suma de goles del equipo '%s' (%d) no coincide con el marcador (%d).";
    public static final String PLAYER_NOT_IN_LINEUP = "El jugador '%s' no está en la alineación de este partido.";
    public static final String TOURNAMENT_NOT_IN_PROGRESS = "El torneo debe estar En Progreso para registrar partidos.";

    private final String field;

    public MatchException(String message) {
        super(message);
        this.field = null;
    }

    public MatchException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
