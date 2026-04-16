package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.validator.EmailValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    @Test
    void validate_withValidData_doesNotThrow() {
        assertDoesNotThrow(() -> EmailValidator.validate("carlos@mail.escuelaing.edu.co"));
        assertDoesNotThrow(() -> EmailValidator.validate("juan@gmail.com"));
    }

    @Test
    void validate_withNullEmail_throwsException() {
        PlayerException ex = assertThrows(PlayerException.class,
                () -> EmailValidator.validate(null));
        assertEquals("email", ex.getField());
    }

    @Test
    void validate_withBlankEmail_throwsException() {
        PlayerException ex = assertThrows(PlayerException.class,
                () -> EmailValidator.validate("   "));
        assertEquals("email", ex.getField());
    }

    @Test
    void validate_withInvalidDomain_throwsException() {
        PlayerException ex = assertThrows(PlayerException.class,
                () -> EmailValidator.validate("user@yahoo.com"));
        assertEquals("email", ex.getField());
    }

    @Test
    void validate_withoutAt_throwsException() {
        PlayerException ex = assertThrows(PlayerException.class,
                () -> EmailValidator.validate("userescuelaing.edu.co"));
        assertEquals("email", ex.getField());
    }

    @Test
    void esCorreoValido_withValid_returnsTrue() {
        assertTrue(EmailValidator.esCorreoValido("test@mail.escuelaing.edu.co"));
        assertTrue(EmailValidator.esCorreoValido("test@gmail.com"));
    }

    @Test
    void esCorreoValido_withInvalid_returnsFalse() {
        assertFalse(EmailValidator.esCorreoValido("test@yahoo.com"));
        assertFalse(EmailValidator.esCorreoValido(null));
    }

    @Test
    void esCorreoInstitucional_withSchool_returnsTrue() {
        assertTrue(EmailValidator.esCorreoInstitucional("admin@mail.escuelaing.edu.co"));
    }

    @Test
    void esCorreoInstitucional_withGmail_returnsFalse() {
        assertFalse(EmailValidator.esCorreoInstitucional("user@gmail.com"));
    }

    @Test
    void esCorreoInstitucional_withNull_returnsFalse() {
        assertFalse(EmailValidator.esCorreoInstitucional(null));
    }
}