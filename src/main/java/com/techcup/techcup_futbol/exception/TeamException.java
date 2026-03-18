package com.techcup.techcup_futbol.exception;

/**
 * Excepción de dominio para operaciones sobre equipos.
 * Centraliza todos los mensajes de error como constantes estáticas.
 */
public class TeamException extends RuntimeException {


    public static final String TEAM_NULL =
            "El equipo no puede ser nulo.";
    public static final String PLAYERS_LIST_NULL =
            "El equipo '%s' tiene la lista de jugadores nula.";
    public static final String PLAYERS_LIST_EMPTY =
            "El equipo '%s' no tiene jugadores.";

    public static final String TEAM_NAME_EMPTY =
            "El nombre del equipo no puede estar vacío.";
    public static final String TEAM_NAME_ALREADY_EXISTS =
            "Ya existe un equipo con el nombre: '%s'.";

    public static final String CAPTAIN_REQUIRED =
            "El equipo '%s' debe tener un capitán asignado.";

    public static final String PLAYER_NULL =
            "El jugador no puede ser nulo.";
    public static final String PLAYER_ALREADY_HAS_TEAM =
            "El jugador '%s' ya pertenece a un equipo.";

    public static final String TEAM_FULL =
            "El equipo '%s' ya alcanzó el máximo de %d jugadores.";

    public static final String PLAYERS_BELOW_MINIMUM =
            "El equipo '%s' tiene %d jugadores. Mínimo requerido: %d.";
    public static final String PLAYERS_ABOVE_MAXIMUM =
            "El equipo '%s' tiene %d jugadores. Máximo permitido: %d.";

    public static final String PLAYER_IN_MULTIPLE_TEAMS =
            "El jugador '%s' está inscrito en %d equipos: %s.";

    public static final String NOT_ENOUGH_STUDENTS =
            "El equipo '%s' tiene %d estudiantes de %d jugadores. Mínimo requerido: %d.";

    public static final String TEAM_NOT_FOUND =
            "No existe equipo con ID: %s";

    public static final String PLAYER_NOT_IN_TEAM =
            "El jugador '%s' no pertenece al equipo '%s'.";


    private final String field;

    public TeamException(String message) {
        super(message);
        this.field = null;
    }

    public TeamException(String field, String message) {
        super(message);
        this.field = field;
    }

    public TeamException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
    }

    public String getField() {
        return field;
    }
}