package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.validator.EmailValidator;
import com.techcup.techcup_futbol.core.exception.PlayerException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmailValidator Tests")
class EmailValidatorTest {

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-EV-01: Correo institucional válido no lanza excepción")
        void correoInstitucionalValido() {
            assertDoesNotThrow(() -> EmailValidator.validate("carlos@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("HP-EV-02: Correo Gmail válido no lanza excepción")
        void correoGmailValido() {
            assertDoesNotThrow(() -> EmailValidator.validate("familiar@gmail.com"));
        }

        @Test
        @DisplayName("HP-EV-03: esCorreoValido retorna true para dominio institucional")
        void esValidoInstitucional() {
            assertTrue(EmailValidator.esCorreoValido("ana.garcia@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("HP-EV-04: esCorreoValido retorna true para Gmail")
        void esValidoGmail() {
            assertTrue(EmailValidator.esCorreoValido("usuario123@gmail.com"));
        }

        @Test
        @DisplayName("HP-EV-05: esCorreoInstitucional retorna true para dominio ECI")
        void esInstitucional() {
            assertTrue(EmailValidator.esCorreoInstitucional("profe@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("HP-EV-06: esCorreoInstitucional retorna false para Gmail")
        void noEsInstitucionalGmail() {
            assertFalse(EmailValidator.esCorreoInstitucional("familiar@gmail.com"));
        }

        @Test
        @DisplayName("HP-EV-07: Correo con puntos y guiones es válido")
        void correoConPuntosYGuiones() {
            assertTrue(EmailValidator.esCorreoValido("juan.perez-2024@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("HP-EV-08: Correo con números en parte local es válido")
        void correoConNumeros() {
            assertTrue(EmailValidator.esCorreoValido("user123@gmail.com"));
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-EV-01: Correo null lanza PlayerException")
        void correoNullLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> EmailValidator.validate(null));
            assertEquals("email", ex.getField());
            assertEquals(PlayerException.EMAIL_NULL_OR_BLANK, ex.getMessage());
        }

        @Test
        @DisplayName("EP-EV-02: Correo vacío lanza PlayerException")
        void correoVacioLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> EmailValidator.validate(""));
            assertEquals(PlayerException.EMAIL_NULL_OR_BLANK, ex.getMessage());
        }

        @Test
        @DisplayName("EP-EV-03: Correo con solo espacios lanza PlayerException")
        void correoSoloEspaciosLanzaExcepcion() {
            assertThrows(PlayerException.class, () -> EmailValidator.validate("   "));
        }

        @Test
        @DisplayName("EP-EV-04: Dominio @hotmail.com lanza PlayerException")
        void dominioHotmailInvalido() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> EmailValidator.validate("user@hotmail.com"));
            assertEquals("email", ex.getField());
            assertTrue(ex.getMessage().contains("user@hotmail.com"));
        }

        @Test
        @DisplayName("EP-EV-05: Dominio @outlook.com lanza PlayerException")
        void dominioOutlookInvalido() {
            assertThrows(PlayerException.class, () -> EmailValidator.validate("user@outlook.com"));
        }

        @Test
        @DisplayName("EP-EV-06: Dominio @yahoo.es lanza PlayerException")
        void dominioYahooInvalido() {
            assertThrows(PlayerException.class, () -> EmailValidator.validate("user@yahoo.es"));
        }

        @Test
        @DisplayName("EP-EV-07: Correo sin arroba retorna false en esCorreoValido")
        void sinArrobaEsInvalido() {
            assertFalse(EmailValidator.esCorreoValido("usuarioescuelaing.edu.co"));
        }

        @Test
        @DisplayName("EP-EV-08: Correo sin parte local retorna false en esCorreoValido")
        void sinParteLocalEsInvalido() {
            assertFalse(EmailValidator.esCorreoValido("@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("EP-EV-09: null en esCorreoValido retorna false sin excepción")
        void nullEnEsCorreoValido() {
            assertFalse(EmailValidator.esCorreoValido(null));
        }

        @Test
        @DisplayName("EP-EV-10: null en esCorreoInstitucional retorna false sin excepción")
        void nullEnEsCorreoInstitucional() {
            assertFalse(EmailValidator.esCorreoInstitucional(null));
        }

        @Test
        @DisplayName("EP-EV-11: Subdominio parcial @escuelaing.edu no es válido")
        void subdominioParcialeNoValido() {
            assertFalse(EmailValidator.esCorreoValido("user@escuelaing.edu"));
        }

        @Test
        @DisplayName("EP-EV-12: @Gmail.com con mayúscula es inválido (case-sensitive en regex)")
        void gmailConMayusculaInvalido() {
            assertFalse(EmailValidator.esCorreoValido("user@Gmail.com"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-EV-01: esCorreoInstitucional es case-insensitive para el dominio")
        void institucionalCaseInsensitive() {
            assertTrue(EmailValidator.esCorreoInstitucional("USER@ESCUELAING.EDU.CO"));
        }

        @Test
        @DisplayName("CS-EV-02: Gmail en esCorreoInstitucional retorna false")
        void gmailNoEsInstitucional() {
            assertFalse(EmailValidator.esCorreoInstitucional("fam@gmail.com"));
        }

        @Test
        @DisplayName("CS-EV-03: validate() no lanza excepción para correo institucional en mayúsculas")
        void institucionalMayusculasEsValido() {
            // La regex acepta letras A-Z, así que email con uppercase en parte local es válido
            assertDoesNotThrow(() -> EmailValidator.validate("Carlos.Rodriguez@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("CS-EV-04: Correo con + es válido (alias Gmail)")
        void correoConMasEsValido() {
            assertTrue(EmailValidator.esCorreoValido("user+test@gmail.com"));
        }

        @Test
        @DisplayName("CS-EV-05: Correo con _ es válido")
        void correoConGuionBajoEsValido() {
            assertTrue(EmailValidator.esCorreoValido("juan_perez@escuelaing.edu.co"));
        }
    }
}
