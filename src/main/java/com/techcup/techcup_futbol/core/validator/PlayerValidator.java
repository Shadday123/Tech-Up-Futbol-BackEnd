package com.techcup.techcup_futbol.core.validator;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Component;

@Component
public class PlayerValidator {

    private final PlayerRepository playerRepository;

    public PlayerValidator(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /** Validación completa desde objeto Player + correo separado. */
    public void validate(Player jugador, String correo) {
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

    public void validateUniqueEmail(String email) {
        if (playerRepository.existsByEmailIgnoreCase(email)) {
            throw new PlayerException("email",
                    String.format(PlayerException.EMAIL_ALREADY_REGISTERED, email));
        }
    }

    public void validateUniqueNumberID(Integer numberID) {
        if (numberID == null) {
            throw new PlayerException("numberID", PlayerException.NUMBER_ID_NULL);
        }
        if (playerRepository.existsByNumberID(numberID)) {
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
