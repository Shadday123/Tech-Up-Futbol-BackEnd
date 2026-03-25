package com.techcup.techcup_futbol.core.exception;

/**
 * Excepción de dominio para operaciones sobre equipos.
 * Centraliza todos los mensajes de error como constantes estáticas.
 */
public class TeamException extends RuntimeException {

    public static final String CONFIG_NOT_FOUND =
            "No existe configuración para el torneo con ID: %s. "
                    + "Use PUT /api/tournaments/{id}/config para crearla.";
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

    public static final String PLAYER_NOT_AVAILABLE =
            "El jugador '%s' no está disponible para recibir invitaciones. "
                    + "Debe activar su disponibilidad antes de unirse a un equipo.";

    public static final String TEAM_REQUIRES_PLAYERS =
            "El equipo '%s' debe tener al menos un jugador. "
                    + "No se puede eliminar al único jugador del equipo.";

    public static final String CAPTAIN_NOT_IN_PLAYERS =
            "El capitán debe estar incluido en la lista inicial de jugadores del equipo.";

    public static final String CANNOT_REMOVE_CAPTAIN =
            "No se puede eliminar al capitán '%s' del equipo. "
                    + "Asigne un nuevo capitán antes de removerlo.";


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