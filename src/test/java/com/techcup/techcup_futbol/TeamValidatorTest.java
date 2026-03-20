package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.validator.TeamValidator;
import com.techcup.techcup_futbol.exception.TeamException;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TeamValidator Tests")
class TeamValidatorTest {

    private Team validTeam;
    private Player captain;

    @BeforeEach
    void setUp() {
        captain = buildStudent("cap@escuelaing.edu.co", 100001, "Capitán");
        validTeam = new Team();
        validTeam.setId(UUID.randomUUID().toString());
        validTeam.setTeamName("Equipo Test");
        validTeam.setShieldUrl("shield.png");
        validTeam.setUniformColors("Rojo");
        validTeam.setCaptain(captain);
        validTeam.setPlayers(buildStudentList(7));
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TV-01: validate() con 7 estudiantes pasa sin errores")
        void sieteEstudiantesValida() {
            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, List.of(validTeam)));
        }

        @Test
        @DisplayName("HP-TV-02: validate() con 12 jugadores (máximo) pasa sin errores")
        void doceJugadoresValida() {
            validTeam.setPlayers(buildStudentList(12));
            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, List.of(validTeam)));
        }

        @Test
        @DisplayName("HP-TV-03: validate() con mezcla permitida (4 estudiantes de 7) pasa")
        void mezclaPermitidaPasa() {
            List<Player> jugadores = new ArrayList<>();
            jugadores.addAll(buildStudentList(4));   // 4 estudiantes
            jugadores.addAll(buildInstitutionalList(3)); // 3 no-estudiantes
            validTeam.setPlayers(jugadores);
            // 4 >= 7/2=3 → cumple
            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, List.of(validTeam)));
        }

        @Test
        @DisplayName("HP-TV-04: validateTeamName con nombre único no lanza excepción")
        void nombreUnicoNoLanzaExcepcion() {
            assertDoesNotThrow(() ->
                    TeamValidator.validateTeamName("Equipo Único", List.of(validTeam)));
        }

        @Test
        @DisplayName("HP-TV-05: validateTeamName con lista null no lanza excepción")
        void listaEquiposNullNoLanzaExcepcion() {
            assertDoesNotThrow(() -> TeamValidator.validateTeamName("Cualquier Nombre", null));
        }

        @Test
        @DisplayName("HP-TV-06: validateCaptain con capitán asignado no lanza excepción")
        void capitanAsignadoNoLanzaExcepcion() {
            assertDoesNotThrow(() -> TeamValidator.validateCaptain(validTeam));
        }

        @Test
        @DisplayName("HP-TV-07: validatePlayerAddition jugador disponible en equipo no lleno")
        void jugadorDisponibleSePuedeAgregar() {
            Player nuevo = buildStudent("nuevo@escuelaing.edu.co", 900001, "Nuevo");
            nuevo.setHaveTeam(false);
            assertDoesNotThrow(() -> TeamValidator.validatePlayerAddition(validTeam, nuevo));
        }

        @Test
        @DisplayName("HP-TV-08: validate() con lista de equipos vacía no lanza por duplicados")
        void listaEquiposVaciaNoLanzaDuplicados() {
            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, List.of(validTeam)));
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TV-01: validate() con equipo null lanza TeamException")
        void equipoNullLanzaExcepcion() {
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(null, new ArrayList<>()));
            assertEquals(TeamException.TEAM_NULL, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TV-02: validate() con players null lanza TeamException")
        void jugadoresNullLanzaExcepcion() {
            validTeam.setPlayers(null);
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(validTeam, List.of(validTeam)));
            assertEquals("players", ex.getField());
            assertTrue(ex.getMessage().contains(validTeam.getTeamName()));
        }

        @Test
        @DisplayName("EP-TV-03: validate() con lista vacía lanza TeamException")
        void jugadoresListaVaciaLanzaExcepcion() {
            validTeam.setPlayers(new ArrayList<>());
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(validTeam, List.of(validTeam)));
            assertEquals("players", ex.getField());
        }

        @Test
        @DisplayName("EP-TV-04: validate() con 6 jugadores (bajo mínimo) lanza TeamException")
        void seisJugadoresBajoMinimoLanzaExcepcion() {
            validTeam.setPlayers(buildStudentList(6));
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(validTeam, List.of(validTeam)));
            assertEquals("players", ex.getField());
            assertTrue(ex.getMessage().contains("6"));
            assertTrue(ex.getMessage().contains("7"));
        }

        @Test
        @DisplayName("EP-TV-05: validate() con 13 jugadores (sobre máximo) lanza TeamException")
        void treceJugadoresSobreMaximoLanzaExcepcion() {
            validTeam.setPlayers(buildStudentList(13));
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(validTeam, List.of(validTeam)));
            assertEquals("players", ex.getField());
            assertTrue(ex.getMessage().contains("13"));
            assertTrue(ex.getMessage().contains("12"));
        }

        @Test
        @DisplayName("EP-TV-06: validate() jugador en dos equipos lanza TeamException")
        void jugadorEnDosEquiposLanzaExcepcion() {
            List<Player> jugadoresCompartidos = buildStudentList(7);
            validTeam.setPlayers(jugadoresCompartidos);

            Team otroEquipo = new Team();
            otroEquipo.setId(UUID.randomUUID().toString());
            otroEquipo.setTeamName("Otro Equipo");
            otroEquipo.setPlayers(new ArrayList<>(jugadoresCompartidos)); // mismo jugador

            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(validTeam, List.of(validTeam, otroEquipo)));
            assertEquals("players", ex.getField());
        }

        @Test
        @DisplayName("EP-TV-07: validate() con 0 estudiantes de 7 jugadores lanza TeamException")
        void sinEstudiantesLanzaExcepcion() {
            validTeam.setPlayers(buildInstitutionalList(7));
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(validTeam, List.of(validTeam)));
            assertEquals("players", ex.getField());
            assertTrue(ex.getMessage().contains("0"));
        }

        @Test
        @DisplayName("EP-TV-08: validate() con 2 estudiantes de 7 (menos del 50%) lanza TeamException")
        void dosEstudiantesDesSieteLanzaExcepcion() {
            List<Player> jugadores = new ArrayList<>();
            jugadores.addAll(buildStudentList(2));
            jugadores.addAll(buildInstitutionalList(5));
            validTeam.setPlayers(jugadores);

            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validate(validTeam, List.of(validTeam)));
            assertEquals("players", ex.getField());
            assertTrue(ex.getMessage().contains("NOT_ENOUGH") || ex.getMessage().contains("2"));
        }

        @Test
        @DisplayName("EP-TV-09: validateTeamName vacío lanza TeamException")
        void nombreVacioLanzaExcepcion() {
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validateTeamName("", new ArrayList<>()));
            assertEquals("teamName", ex.getField());
            assertEquals(TeamException.TEAM_NAME_EMPTY, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TV-10: validateTeamName null lanza TeamException")
        void nombreNullLanzaExcepcion() {
            assertThrows(TeamException.class,
                    () -> TeamValidator.validateTeamName(null, new ArrayList<>()));
        }

        @Test
        @DisplayName("EP-TV-11: validateTeamName nombre duplicado (case-insensitive) lanza TeamException")
        void nombreDuplicadoCaseInsensitiveLanzaExcepcion() {
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validateTeamName("EQUIPO TEST", List.of(validTeam)));
            assertEquals("teamName", ex.getField());
            assertTrue(ex.getMessage().contains("EQUIPO TEST"));
        }

        @Test
        @DisplayName("EP-TV-12: validateCaptain sin capitán lanza TeamException")
        void sinCapitanLanzaExcepcion() {
            validTeam.setCaptain(null);
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validateCaptain(validTeam));
            assertEquals("captain", ex.getField());
        }

        @Test
        @DisplayName("EP-TV-13: validatePlayerAddition jugador null lanza TeamException")
        void jugadorNullLanzaExcepcion() {
            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validatePlayerAddition(validTeam, null));
            assertEquals("player", ex.getField());
            assertEquals(TeamException.PLAYER_NULL, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TV-14: validatePlayerAddition jugador con equipo ya asignado lanza TeamException")
        void jugadorConEquipoLanzaExcepcion() {
            Player ocupado = buildStudent("ocupado@escuelaing.edu.co", 800001, "Ocupado");
            ocupado.setHaveTeam(true);

            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validatePlayerAddition(validTeam, ocupado));
            assertEquals("player", ex.getField());
            assertTrue(ex.getMessage().contains("Ocupado"));
        }

        @Test
        @DisplayName("EP-TV-15: validatePlayerAddition equipo con 12 jugadores lanza TeamException")
        void equipoLlenoLanzaExcepcion() {
            validTeam.setPlayers(buildStudentList(12));
            Player extra = buildStudent("extra@escuelaing.edu.co", 800002, "Extra");
            extra.setHaveTeam(false);

            TeamException ex = assertThrows(TeamException.class,
                    () -> TeamValidator.validatePlayerAddition(validTeam, extra));
            assertEquals("players", ex.getField());
            assertTrue(ex.getMessage().contains("12"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TV-01: 7 estudiantes de 7 (100%) cumple la regla del 50%")
        void todosEstudiantesValida() {
            validTeam.setPlayers(buildStudentList(7));
            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, List.of(validTeam)));
        }

        @Test
        @DisplayName("CS-TV-02: Exactamente mitad (3 de 7 no cuenta, necesita floor(7/2)=3 estudiantes)")
        void mitadExactaEstudiantes() {
            // floor(7/2) = 3. Con 3 estudiantes y 4 no-estudiantes PASA (3 >= 3)
            List<Player> jugadores = new ArrayList<>();
            jugadores.addAll(buildStudentList(3));
            jugadores.addAll(buildInstitutionalList(4));
            validTeam.setPlayers(jugadores);
            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, List.of(validTeam)));
        }

        @Test
        @DisplayName("CS-TV-03: validateTeamName nombre exactamente igual (case-sensitive fail) lanza excepción")
        void nombreExactoIgualLanzaExcepcion() {
            assertThrows(TeamException.class,
                    () -> TeamValidator.validateTeamName("Equipo Test", List.of(validTeam)));
        }

        @Test
        @DisplayName("CS-TV-04: validateTeamName nombre diferente en lista no lanza excepción")
        void nombreDiferentePasa() {
            assertDoesNotThrow(() ->
                    TeamValidator.validateTeamName("Equipo Distinto", List.of(validTeam)));
        }

        @Test
        @DisplayName("CS-TV-05: jugadores con ID null en otro equipo no rompe la validación de duplicados")
        void jugadorConIdNullNoRompe() {
            Player sinId = buildStudent("s@escuelaing.edu.co", 700001, "Sin ID");
            sinId.setId(null);
            Team otro = new Team();
            otro.setId("otro");
            otro.setTeamName("Otro");
            otro.setPlayers(List.of(sinId));

            assertDoesNotThrow(() ->
                    TeamValidator.validate(validTeam, List.of(validTeam, otro)));
        }

        @Test
        @DisplayName("CS-TV-06: equipo nulo en la lista de todos no rompe la validación de duplicados")
        void equipoNuloEnListaNoRompe() {
            List<Team> conNull = new ArrayList<>();
            conNull.add(validTeam);
            conNull.add(null);

            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, conNull));
        }

        @Test
        @DisplayName("CS-TV-07: 12 estudiantes (máximo y todos del programa) es el escenario ideal")
        void doceEstudiantesMaximoIdeal() {
            validTeam.setPlayers(buildStudentList(12));
            assertDoesNotThrow(() -> TeamValidator.validate(validTeam, List.of(validTeam)));
        }

        @Test
        @DisplayName("CS-TV-08: validatePlayerAddition en equipo con 0 jugadores permite agregar")
        void equipoVacioPermiteAgregar() {
            validTeam.setPlayers(new ArrayList<>());
            Player nuevo = buildStudent("libre@escuelaing.edu.co", 600001, "Libre");
            nuevo.setHaveTeam(false);
            assertDoesNotThrow(() -> TeamValidator.validatePlayerAddition(validTeam, nuevo));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static int counter = 10000;

    private StudentPlayer buildStudent(String email, int numberID, String name) {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname(name);
        p.setEmail(email);
        p.setNumberID(numberID);
        p.setAge(20);
        p.setGender("Masculino");
        p.setSemester(4);
        p.setHaveTeam(false);
        return p;
    }

    private List<Player> buildStudentList(int count) {
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            counter++;
            StudentPlayer p = buildStudent("st" + counter + "@escuelaing.edu.co", counter, "Student " + counter);
            list.add(p);
        }
        return list;
    }

    private List<Player> buildInstitutionalList(int count) {
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            counter++;
            InstitutionalPlayer p = new InstitutionalPlayer();
            p.setId(UUID.randomUUID().toString());
            p.setFullname("Inst " + counter);
            p.setEmail("inst" + counter + "@gmail.com");
            p.setNumberID(counter);
            p.setAge(25);
            p.setGender("Masculino");
            p.setHaveTeam(false);
            list.add(p);
        }
        return list;
    }
}
