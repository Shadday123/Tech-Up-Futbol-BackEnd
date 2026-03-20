package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.validator.PlayerValidator;
import com.techcup.techcup_futbol.exception.PlayerException;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlayerValidator Tests")
class PlayerValidatorTest {

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PV-01: Validación completa de jugador estudiante sin errores")
        void validacionCompletaEstudiante() {
            StudentPlayer p = buildStudent("carlos@escuelaing.edu.co", 111111, "Carlos", 22);
            assertDoesNotThrow(() -> PlayerValidator.validate(p, "carlos@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("HP-PV-02: Validación completa de familiar con Gmail sin errores")
        void validacionCompletaFamiliar() {
            RelativePlayer p = buildRelative("fam@gmail.com", 222222, "Familiar Test", 30);
            assertDoesNotThrow(() -> PlayerValidator.validate(p, "fam@gmail.com"));
        }

        @Test
        @DisplayName("HP-PV-03: validateFullname con nombre válido no lanza excepción")
        void nombreValido() {
            assertDoesNotThrow(() -> PlayerValidator.validateFullname("Juan Pérez García"));
        }

        @Test
        @DisplayName("HP-PV-04: validateAge con edad 15 (mínimo) no lanza excepción")
        void edadMinima() {
            assertDoesNotThrow(() -> PlayerValidator.validateAge(15));
        }

        @Test
        @DisplayName("HP-PV-05: validateAge con edad 110 (máximo) no lanza excepción")
        void edadMaxima() {
            assertDoesNotThrow(() -> PlayerValidator.validateAge(110));
        }

        @Test
        @DisplayName("HP-PV-06: validateAge con edad intermedia no lanza excepción")
        void edadIntermedia() {
            assertDoesNotThrow(() -> PlayerValidator.validateAge(45));
        }

        @Test
        @DisplayName("HP-PV-07: validateUniqueEmail con correo nuevo no lanza excepción")
        void emailUnicoNoLanzaExcepcion() {
            assertDoesNotThrow(() ->
                    PlayerValidator.validateUniqueEmail("nuevo@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("HP-PV-08: validateUniqueNumberID con ID nuevo no lanza excepción")
        void idUnicoNoLanzaExcepcion() {
            assertDoesNotThrow(() -> PlayerValidator.validateUniqueNumberID(999999));
        }

        @Test
        @DisplayName("HP-PV-09: validateDorsal con 1 (mínimo) no lanza excepción")
        void dorsalMinimo() {
            assertDoesNotThrow(() -> PlayerValidator.validateDorsal(1));
        }

        @Test
        @DisplayName("HP-PV-10: validateDorsal con 99 (máximo) no lanza excepción")
        void dorsalMaximo() {
            assertDoesNotThrow(() -> PlayerValidator.validateDorsal(99));
        }

        @Test
        @DisplayName("HP-PV-11: validateDorsal con valor intermedio no lanza excepción")
        void dorsalIntermedio() {
            assertDoesNotThrow(() -> PlayerValidator.validateDorsal(10));
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-PV-01: validateFullname con null lanza PlayerException")
        void nombreNullLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateFullname(null));
            assertEquals("fullname", ex.getField());
            assertEquals(PlayerException.FULLNAME_EMPTY, ex.getMessage());
        }

        @Test
        @DisplayName("EP-PV-02: validateFullname con string vacío lanza PlayerException")
        void nombreVacioLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateFullname(""));
            assertEquals(PlayerException.FULLNAME_EMPTY, ex.getMessage());
        }

        @Test
        @DisplayName("EP-PV-03: validateFullname con solo espacios lanza PlayerException")
        void nombreSoloEspaciosLanzaExcepcion() {
            assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateFullname("   "));
        }

        @Test
        @DisplayName("EP-PV-04: validateAge con 14 (menor al mínimo) lanza PlayerException")
        void edadBajoMinimoLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateAge(14));
            assertEquals("age", ex.getField());
            assertTrue(ex.getMessage().contains("14"));
        }

        @Test
        @DisplayName("EP-PV-05: validateAge con 111 (mayor al máximo) lanza PlayerException")
        void edadSobreMaximoLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateAge(111));
            assertEquals("age", ex.getField());
            assertTrue(ex.getMessage().contains("111"));
        }

        @Test
        @DisplayName("EP-PV-06: validateAge con 0 lanza PlayerException")
        void edadCeroLanzaExcepcion() {
            assertThrows(PlayerException.class, () -> PlayerValidator.validateAge(0));
        }

        @Test
        @DisplayName("EP-PV-07: validateAge con negativo lanza PlayerException")
        void edadNegativaLanzaExcepcion() {
            assertThrows(PlayerException.class, () -> PlayerValidator.validateAge(-5));
        }

        @Test
        @DisplayName("EP-PV-08: validateUniqueEmail lanza excepción si ya existe en DataStore")
        void emailDuplicadoLanzaExcepcion() {
            StudentPlayer existente = buildStudent("dup@escuelaing.edu.co", 111001, "Existente", 20);
            DataStore.jugadores.put("J-DUP", existente);

            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateUniqueEmail("dup@escuelaing.edu.co"));
            assertEquals("email", ex.getField());
            assertTrue(ex.getMessage().contains("dup@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("EP-PV-09: validateUniqueEmail es case-insensitive para duplicados")
        void emailDuplicadoCaseInsensitive() {
            StudentPlayer existente = buildStudent("test@escuelaing.edu.co", 111002, "Existente", 20);
            DataStore.jugadores.put("J-TEST", existente);

            assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateUniqueEmail("TEST@ESCUELAING.EDU.CO"));
        }

        @Test
        @DisplayName("EP-PV-10: validateUniqueNumberID con null lanza PlayerException")
        void numberIDNullLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateUniqueNumberID(null));
            assertEquals("numberID", ex.getField());
            assertEquals(PlayerException.NUMBER_ID_NULL, ex.getMessage());
        }

        @Test
        @DisplayName("EP-PV-11: validateUniqueNumberID duplicado lanza PlayerException")
        void numberIDDuplicadoLanzaExcepcion() {
            StudentPlayer existente = buildStudent("otro@escuelaing.edu.co", 555555, "Otro", 20);
            DataStore.jugadores.put("J-OTRO", existente);

            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateUniqueNumberID(555555));
            assertEquals("numberID", ex.getField());
            assertTrue(ex.getMessage().contains("555555"));
        }

        @Test
        @DisplayName("EP-PV-12: validateDorsal con 0 lanza PlayerException")
        void dorsalCeroLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateDorsal(0));
            assertEquals("dorsal", ex.getField());
            assertTrue(ex.getMessage().contains("0"));
        }

        @Test
        @DisplayName("EP-PV-13: validateDorsal con 100 lanza PlayerException")
        void dorsal100LanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateDorsal(100));
            assertEquals("dorsal", ex.getField());
            assertTrue(ex.getMessage().contains("100"));
        }

        @Test
        @DisplayName("EP-PV-14: validateDorsal negativo lanza PlayerException")
        void dorsalNegativoLanzaExcepcion() {
            assertThrows(PlayerException.class, () -> PlayerValidator.validateDorsal(-1));
        }

        @Test
        @DisplayName("EP-PV-15: validate() completo falla si correo tiene dominio inválido")
        void validacionCompletaFallaConDominioInvalido() {
            StudentPlayer p = buildStudent("test@hotmail.com", 777001, "Test", 20);
            assertThrows(PlayerException.class,
                    () -> PlayerValidator.validate(p, "test@hotmail.com"));
        }

        @Test
        @DisplayName("EP-PV-16: validate() completo falla si nombre está vacío")
        void validacionCompletaFallaConNombreVacio() {
            StudentPlayer p = buildStudent("test@escuelaing.edu.co", 777002, "", 20);
            assertThrows(PlayerException.class,
                    () -> PlayerValidator.validate(p, "test@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("EP-PV-17: validate() completo falla si edad es inválida")
        void validacionCompletaFallaConEdadInvalida() {
            StudentPlayer p = buildStudent("test@escuelaing.edu.co", 777003, "Test User", 10);
            assertThrows(PlayerException.class,
                    () -> PlayerValidator.validate(p, "test@escuelaing.edu.co"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-PV-01: validate() aplica validaciones en orden — nombre vacío tiene prioridad")
        void validacionEnOrdenNombrePrimero() {
            StudentPlayer p = buildStudent("invalido@hotmail.com", 999001, "", 10);
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> PlayerValidator.validate(p, "invalido@hotmail.com"));
            // Debe fallar en nombre primero (FULLNAME_EMPTY), no en edad ni correo
            assertEquals("fullname", ex.getField());
        }

        @Test
        @DisplayName("CS-PV-02: DataStore vacío — validateUniqueEmail siempre pasa")
        void dataStoreVacioEmailSiemprePasa() {
            assertDoesNotThrow(() ->
                    PlayerValidator.validateUniqueEmail("cualquier@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("CS-PV-03: DataStore vacío — validateUniqueNumberID siempre pasa")
        void dataStoreVacioIDSiemprePasa() {
            assertDoesNotThrow(() -> PlayerValidator.validateUniqueNumberID(123456));
        }

        @Test
        @DisplayName("CS-PV-04: validateUniqueEmail no falla con jugador que tiene email null")
        void jugadorConEmailNullNoRompeValidacion() {
            Player sinEmail = new StudentPlayer();
            sinEmail.setEmail(null);
            DataStore.jugadores.put("J-NULL-EMAIL", sinEmail);

            assertDoesNotThrow(() ->
                    PlayerValidator.validateUniqueEmail("nuevo@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("CS-PV-05: validateDorsal con valor límite exacto 1 y 99 son válidos")
        void dorsalEnLimitesExactos() {
            assertDoesNotThrow(() -> PlayerValidator.validateDorsal(1));
            assertDoesNotThrow(() -> PlayerValidator.validateDorsal(99));
        }

        @Test
        @DisplayName("CS-PV-06: validateAge con 15 y 110 (límites exactos) son válidos")
        void edadEnLimitesExactos() {
            assertDoesNotThrow(() -> PlayerValidator.validateAge(15));
            assertDoesNotThrow(() -> PlayerValidator.validateAge(110));
        }

        @Test
        @DisplayName("CS-PV-07: Múltiples jugadores en DataStore — detección correcta de duplicado")
        void multiplesJugadoresDetectaDuplicado() {
            for (int i = 1; i <= 5; i++) {
                StudentPlayer p = buildStudent("user" + i + "@escuelaing.edu.co", 100000 + i, "User " + i, 20);
                DataStore.jugadores.put("J-" + i, p);
            }
            assertThrows(PlayerException.class,
                    () -> PlayerValidator.validateUniqueEmail("user3@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("CS-PV-08: Múltiples jugadores en DataStore — nuevo email pasa")
        void multiplesJugadoresNuevoEmailPasa() {
            for (int i = 1; i <= 5; i++) {
                StudentPlayer p = buildStudent("user" + i + "@escuelaing.edu.co", 100010 + i, "User " + i, 20);
                DataStore.jugadores.put("J-X" + i, p);
            }
            assertDoesNotThrow(() ->
                    PlayerValidator.validateUniqueEmail("nuevo@escuelaing.edu.co"));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private StudentPlayer buildStudent(String email, int numberID, String name, int age) {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname(name);
        p.setEmail(email);
        p.setNumberID(numberID);
        p.setAge(age);
        p.setGender("Masculino");
        p.setSemester(3);
        return p;
    }

    private RelativePlayer buildRelative(String email, int numberID, String name, int age) {
        RelativePlayer p = new RelativePlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname(name);
        p.setEmail(email);
        p.setNumberID(numberID);
        p.setAge(age);
        p.setGender("Femenino");
        return p;
    }
}
