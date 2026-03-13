package com.techcup.techcup_futbol.validator;

import com.techcup.techcup_futbol.Controller.dto.PlayerRegistrationRequest;
import com.techcup.techcup_futbol.model.DataStore;
import java.util.regex.Pattern;

public class PlayerValidator {


    private static final String SCHOOL_DOMAIN = "@escuelaing.edu.co";
    private static final String GMAIL_DOMAIN = "@gmail.com";

    public static class PlayerException extends Exception {

        public PlayerException(String message) {
            super(message);
        }
    }

    public static void validate(PlayerRegistrationRequest request) throws PlayerException {
        validateEmailDomain(request.email());
        validateUniqueness(request.numberID());
    }

    /**
     * Valida que el correo pertenezca a los dominios permitidos.
     */
    public static void validateEmailDomain(String email) throws PlayerException {

        String lowerEmail = email.toLowerCase();

        if (!lowerEmail.endsWith(SCHOOL_DOMAIN) && !lowerEmail.endsWith(GMAIL_DOMAIN)) {
            throw new PlayerException(
                    "Email must belong to @escuelaing.edu.co or @gmail.com"
            );
        }
    }

    /**
     * Valida que el número de identificación no esté repetido.
     */
    public static void validateUniqueness(Integer numberID) throws PlayerException {

        boolean exists = DataStore.jugadores.values().stream()
                .anyMatch(p -> p.getNumberID() == numberID);

        if (exists) {
            throw new PlayerException(
                    "A player with this ID number is already registered."
            );
        }
    }

    /**
     * Valida que el dorsal esté en el rango permitido.
     */
    public static void validateDorsal(int dorsal) throws PlayerException {

        if (dorsal < 1 || dorsal > 99) {
            throw new PlayerException(
                    "Dorsal number must be between 1 and 99."
            );
        }
    }
}

