package com.techcup.techcup_futbol.core.validator;

import com.techcup.techcup_futbol.Controller.dto.PlayerRegistrationRequest;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.exception.PlayerException;

public class PlayerValidator {

    private PlayerValidator() {}

    // ── Puntos de entrada ─────────────────────────────────────────────────────

    /** Validación completa desde DTO. */
    public static void validate(PlayerRegistrationRequest request) {
        validateFullname(request.fullname());
        validateAge(request.age());
        EmailValidator.validate(request.email());
        validateUniqueEmail(request.email());
        validateUniqueNumberID(request.numberID());
    }

    /** Validación completa desde objeto Player + correo separado. */
    public static void validate(Player jugador, String correo) {
        validateFullname(jugador.getFullname());
        validateAge(jugador.getAge());
        EmailValidator.validate(correo);
        validateUniqueEmail(correo);
        validateUniqueNumberID(jugador.getNumberID());
    }


    public static void validateFullname(String fullname) {
        if (fullname == null || fullname.isBlank()) {
            throw new PlayerException("fullname", PlayerException.FULLNAME_EMPTY);
        }
    }

    public static void validateAge(int age) {
        if (age < 15 || age > 110) {
            throw new PlayerException("age",
                    String.format(PlayerException.AGE_OUT_OF_RANGE, age));
        }
    }

    public static void validateEmailDomain(String email) {
        EmailValidator.validate(email);
    }

    public static void validateUniqueEmail(String email) {
        boolean exists = DataStore.jugadores.values().stream()
                .anyMatch(p -> p.getEmail() != null
                        && p.getEmail().equalsIgnoreCase(email));
        if (exists) {
            throw new PlayerException("email",
                    String.format(PlayerException.EMAIL_ALREADY_REGISTERED, email));
        }
    }
    public static void validateUniqueNumberID(Integer numberID) {
        if (numberID == null) {
            throw new PlayerException("numberID", PlayerException.NUMBER_ID_NULL);
        }
        boolean exists = DataStore.jugadores.values().stream()
                .anyMatch(p -> numberID.equals(p.getNumberID()));
        if (exists) {
            throw new PlayerException("numberID",
                    String.format(PlayerException.NUMBER_ID_ALREADY_REGISTERED, numberID));
        }
    }

    public static void validateDorsal(int dorsal) {
        if (dorsal < 1 || dorsal > 99) {
            throw new PlayerException("dorsal",
                    String.format(PlayerException.DORSAL_OUT_OF_RANGE, dorsal));
        }
    }
}