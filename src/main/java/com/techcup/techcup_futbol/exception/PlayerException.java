package com.techcup.techcup_futbol.exception;

public class PlayerException extends RuntimeException {

    public static final String FULLNAME_EMPTY =
            "El nombre completo del jugador no puede estar vacío.";
    public static final String AGE_OUT_OF_RANGE =
            "La edad del jugador debe estar entre 15 y 60 años. Valor recibido: %d";
    public static final String EMAIL_NULL_OR_BLANK =
            "El correo no puede ser nulo o vacío.";
    public static final String EMAIL_INVALID_DOMAIN =
            "Correo no válido. Solo se permiten dominios: @escuelaing.edu.co o @gmail.com. Valor recibido: %s";
    public static final String EMAIL_ALREADY_REGISTERED =
            "El correo '%s' ya se encuentra registrado.";
    public static final String NUMBER_ID_NULL =
            "El número de ID no puede ser nulo.";
    public static final String NUMBER_ID_ALREADY_REGISTERED =
            "Ya existe un jugador registrado con el número de ID: %d";
    public static final String DORSAL_OUT_OF_RANGE =
            "El dorsal debe estar entre 1 y 99. Valor recibido: %d";

    public static final String PLAYER_NOT_FOUND =
            "No existe un jugador con ID: %s";


    public static final String PLAYER_ALREADY_AVAILABLE =
            "El jugador '%s' ya está disponible (sin equipo).";
    public static final String PLAYER_ALREADY_UNAVAILABLE =
            "El jugador '%s' ya está marcado como con equipo.";

    public static final String PHOTO_URL_EMPTY =
            "La URL de la foto no puede estar vacía.";

    public static final String PLAYER_ID_NULL =
            "El ID interno del jugador no puede ser nulo al registrar.";

    private final String field;

    public PlayerException(String message) {
        super(message);
        this.field = null;
    }

    public PlayerException(String field, String message) {
        super(message);
        this.field = field;
    }

    public PlayerException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
    }

    public String getField() {
        return field;
    }
}