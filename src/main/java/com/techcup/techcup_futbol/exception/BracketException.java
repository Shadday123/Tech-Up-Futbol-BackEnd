package com.techcup.techcup_futbol.exception;

public class BracketException extends RuntimeException {

    public static final String TOURNAMENT_NOT_FOUND = "No existe el torneo con ID: %s";
    public static final String BRACKET_ALREADY_EXISTS = "El torneo '%s' ya tiene llaves generadas.";
    public static final String BRACKET_NOT_FOUND = "No se han generado llaves para el torneo '%s'.";
    public static final String RESULTS_ALREADY_REGISTERED = "No se puede regenerar el bracket porque ya existen resultados registrados.";
    public static final String NOT_ENOUGH_TEAMS = "Se necesitan al menos 2 equipos para generar las llaves. Disponibles: %d";
    public static final String TEAMS_NOT_POWER_OF_TWO = "La cantidad de equipos debe ser potencia de 2 (2, 4, 8, 16). Recibidos: %d";
    public static final String MATCH_NOT_FOUND = "No existe un partido con ID: %s en las llaves.";

    private final String field;

    public BracketException(String message) {
        super(message);
        this.field = null;
    }

    public BracketException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
