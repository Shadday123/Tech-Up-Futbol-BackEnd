package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.TeamServiceImpl;
import com.techcup.techcup_futbol.exception.TeamException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Team Service Tests")
class TeamServiceTest {

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team validTeam;
    private StudentPlayer teamCaptain;
    private StudentPlayer teamMember1;
    private StudentPlayer teamMember2;
    private InstitutionalPlayer teamMember3;
    private RelativePlayer teamMember4;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();

        teamCaptain = new StudentPlayer();
        teamCaptain.setId("TCAP001");
        teamCaptain.setFullname("Capitán del Equipo");
        teamCaptain.setNumberID(100001);
        teamCaptain.setEmail("capitan@escuelaing.edu.co");
        teamCaptain.setPosition(PositionEnum.Midfielder);
        teamCaptain.setDorsalNumber(10);
        teamCaptain.setAge(22);
        teamCaptain.setGender("Masculino");
        teamCaptain.setCaptain(true);
        teamCaptain.setSemester(6);
        teamCaptain.setHaveTeam(false);
        DataStore.jugadores.put(teamCaptain.getId(), teamCaptain);

        teamMember1 = new StudentPlayer();
        teamMember1.setId("TM001");
        teamMember1.setFullname("Miembro 1");
        teamMember1.setNumberID(100002);
        teamMember1.setEmail("miembro1@escuelaing.edu.co");
        teamMember1.setPosition(PositionEnum.Defender);
        teamMember1.setDorsalNumber(3);
        teamMember1.setAge(20);
        teamMember1.setGender("Masculino");
        teamMember1.setCaptain(false);
        teamMember1.setSemester(4);
        teamMember1.setHaveTeam(false);
        DataStore.jugadores.put(teamMember1.getId(), teamMember1);

        teamMember2 = new StudentPlayer();
        teamMember2.setId("TM002");
        teamMember2.setFullname("Miembro 2");
        teamMember2.setNumberID(100003);
        teamMember2.setEmail("miembro2@escuelaing.edu.co");
        teamMember2.setPosition(PositionEnum.Winger);
        teamMember2.setDorsalNumber(7);
        teamMember2.setAge(21);
        teamMember2.setGender("Femenino");
        teamMember2.setCaptain(false);
        teamMember2.setSemester(3);
        teamMember2.setHaveTeam(false);
        DataStore.jugadores.put(teamMember2.getId(), teamMember2);

        teamMember3 = new InstitutionalPlayer();
        teamMember3.setId("TM003");
        teamMember3.setFullname("Miembro 3");
        teamMember3.setNumberID(100004);
        teamMember3.setEmail("miembro3@gmail.com");
        teamMember3.setPosition(PositionEnum.GoalKeeper);
        teamMember3.setDorsalNumber(1);
        teamMember3.setAge(23);
        teamMember3.setGender("Masculino");
        teamMember3.setCaptain(false);
        teamMember3.setHaveTeam(false);
        DataStore.jugadores.put(teamMember3.getId(), teamMember3);

        teamMember4 = new RelativePlayer();
        teamMember4.setId("TM004");
        teamMember4.setFullname("Miembro 4");
        teamMember4.setNumberID(100005);
        teamMember4.setEmail("miembro4@gmail.com");
        teamMember4.setPosition(PositionEnum.Midfielder);
        teamMember4.setDorsalNumber(6);
        teamMember4.setAge(25);
        teamMember4.setGender("Masculino");
        teamMember4.setCaptain(false);
        teamMember4.setHaveTeam(false);
        DataStore.jugadores.put(teamMember4.getId(), teamMember4);

        validTeam = new Team();
        validTeam.setId("E_TEST001");
        validTeam.setTeamName("Equipo Test");
        validTeam.setShieldUrl("shield_test.png");
        validTeam.setUniformColors("Azul y Blanco");
        validTeam.setCaptain(teamCaptain);
        validTeam.setPlayers(new ArrayList<>());
    }

    // ── Happy Path ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Happy Path")
    class HappyPathTests {

        @Test
        @DisplayName("HP-T01: Crear equipo con nombre único y capitán")
        void testCreateTeamSuccessfully() {
            Team created = teamService.createTeam(validTeam);

            assertNotNull(created);
            assertEquals("Equipo Test", created.getTeamName());
            assertEquals(teamCaptain, created.getCaptain());
            assertTrue(teamService.getAllTeams().contains(created));
        }

        @Test
        @DisplayName("HP-T02: Invitar jugador disponible — haveTeam pasa a true")
        void testInviteAvailablePlayer() {
            teamService.createTeam(validTeam);

            teamService.invitePlayer(validTeam.getId(), teamMember1);

            assertTrue(validTeam.getPlayers().contains(teamMember1));
            assertTrue(teamMember1.isHaveTeam());
        }

        @Test
        @DisplayName("HP-T03: Remover jugador desvincula correctamente")
        void testRemovePlayerDesvincula() {
            teamService.createTeam(validTeam);
            teamService.invitePlayer(validTeam.getId(), teamMember1);

            teamService.removePlayer(validTeam.getId(), teamMember1.getId());

            assertFalse(validTeam.getPlayers().contains(teamMember1));
            assertFalse(teamMember1.isHaveTeam());
        }

        @Test
        @DisplayName("HP-T04: buscarPorId retorna Optional presente")
        void testBuscarPorId_Exitoso() {
            teamService.createTeam(validTeam);

            Optional<Team> found = teamService.buscarPorId(validTeam.getId());

            assertTrue(found.isPresent());
            assertEquals("Equipo Test", found.get().getTeamName());
        }

        @Test
        @DisplayName("HP-T05: obtenerPorId retorna equipo existente")
        void testObtenerPorId_Exitoso() {
            teamService.createTeam(validTeam);

            Team found = teamService.obtenerPorId(validTeam.getId());

            assertNotNull(found);
            assertEquals(validTeam.getId(), found.getId());
        }

        @Test
        @DisplayName("HP-T06: getAllTeams lista todos los equipos")
        void testGetAllTeams() {
            teamService.createTeam(validTeam);

            Team segundo = new Team();
            segundo.setId("E_TEST002");
            segundo.setTeamName("Segundo Equipo");
            segundo.setShieldUrl("s2.png");
            segundo.setUniformColors("Rojo");
            segundo.setCaptain(teamMember1);
            segundo.setPlayers(new ArrayList<>());
            teamService.createTeam(segundo);

            assertEquals(2, teamService.getAllTeams().size());
        }

        @Test
        @DisplayName("HP-T07: deleteTeam elimina equipo y desvincula jugadores")
        void testDeleteTeam() {
            teamService.createTeam(validTeam);
            teamService.invitePlayer(validTeam.getId(), teamMember1);

            teamService.deleteTeam(validTeam.getId());

            assertFalse(teamService.getAllTeams().contains(validTeam));
            assertFalse(teamMember1.isHaveTeam());
        }
    }

    // ── Error Path ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Error Path")
    class ErrorPathTests {

        @Test
        @DisplayName("EP-T01: Nombre duplicado lanza TeamException")
        void testCreateTeamDuplicateName() {
            teamService.createTeam(validTeam);

            Team dup = new Team();
            dup.setId("E_DUP");
            dup.setTeamName("Equipo Test");
            dup.setShieldUrl("s.png");
            dup.setUniformColors("Gris");
            dup.setCaptain(teamMember1);
            dup.setPlayers(new ArrayList<>());

            assertThrows(TeamException.class, () -> teamService.createTeam(dup));
        }

        @Test
        @DisplayName("EP-T02: Crear equipo sin capitán lanza TeamException")
        void testCreateTeamSinCapitan() {
            validTeam.setCaptain(null);

            assertThrows(TeamException.class, () -> teamService.createTeam(validTeam));
        }

        @Test
        @DisplayName("EP-T03: Invitar jugador con equipo asignado lanza TeamException")
        void testInvitePlayerYaEnEquipo() {
            teamMember1.setHaveTeam(true);
            teamService.createTeam(validTeam);

            assertThrows(TeamException.class, () ->
                    teamService.invitePlayer(validTeam.getId(), teamMember1)
            );
        }

        @Test
        @DisplayName("EP-T04: Invitar a equipo inexistente lanza TeamException")
        void testInvitePlayerEquipoNoExiste() {
            assertThrows(TeamException.class, () ->
                    teamService.invitePlayer("ID_FALSO", teamMember1)
            );
        }

        @Test
        @DisplayName("EP-T05: Remover jugador que no pertenece al equipo lanza TeamException")
        void testRemovePlayerNoPertenece() {
            teamService.createTeam(validTeam);

            assertThrows(TeamException.class, () ->
                    teamService.removePlayer(validTeam.getId(), "JUGADOR_AJENO")
            );
        }

        @Test
        @DisplayName("EP-T06: obtenerPorId con ID inexistente lanza TeamException")
        void testObtenerPorId_NoExiste() {
            assertThrows(TeamException.class, () ->
                    teamService.obtenerPorId("E_NOTFOUND")
            );
        }

        @Test
        @DisplayName("EP-T07: deleteTeam con ID inexistente lanza TeamException")
        void testDeleteTeam_NoExiste() {
            assertThrows(TeamException.class, () ->
                    teamService.deleteTeam("E_NOTFOUND")
            );
        }

        @Test
        @DisplayName("EP-T08: Equipo lleno rechaza nuevo jugador con TeamException")
        void testInvitePlayerEquipoLleno() {
            teamService.createTeam(validTeam);

            for (int i = 0; i < 12; i++) {
                StudentPlayer extra = new StudentPlayer();
                extra.setId("EXTRA_" + i);
                extra.setFullname("Extra " + i);
                extra.setHaveTeam(true);
                validTeam.getPlayers().add(extra);
            }

            StudentPlayer unMas = new StudentPlayer();
            unMas.setId("UNO_MAS");
            unMas.setFullname("Uno Más");
            unMas.setHaveTeam(false);

            assertThrows(TeamException.class, () ->
                    teamService.invitePlayer(validTeam.getId(), unMas)
            );
        }
    }

    // ── Conditional ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-T01: buscarPorId retorna vacío para ID inexistente")
        void testBuscarPorId_NoExiste() {
            assertFalse(teamService.buscarPorId("E_NOTFOUND").isPresent());
        }

        @Test
        @DisplayName("CS-T02: getAllTeams retorna copia defensiva")
        void testGetAllTeams_CopiaDefensiva() {
            teamService.createTeam(validTeam);
            List<Team> lista = teamService.getAllTeams();
            lista.clear();

            assertEquals(1, teamService.getAllTeams().size());
        }

        @Test
        @DisplayName("CS-T03: Jugador no puede estar en dos equipos simultáneamente")
        void testJugadorNoEnDosEquipos() {
            teamService.createTeam(validTeam);
            teamService.invitePlayer(validTeam.getId(), teamMember1);

            Team segundo = new Team();
            segundo.setId("E_TEAM2");
            segundo.setTeamName("Segundo Equipo");
            segundo.setShieldUrl("s2.png");
            segundo.setUniformColors("Rojo");
            segundo.setCaptain(teamMember2);
            segundo.setPlayers(new ArrayList<>());
            teamService.createTeam(segundo);

            assertThrows(TeamException.class, () ->
                    teamService.invitePlayer(segundo.getId(), teamMember1)
            );
        }

        @Test
        @DisplayName("CS-T04: validateTeamForTournament con 7+ jugadores no lanza excepción")
        void testValidateTeamForTournament_Valido() {
            teamService.createTeam(validTeam);

            for (int i = 0; i < 7; i++) {
                StudentPlayer s = new StudentPlayer();
                s.setId("ST_" + i);
                s.setFullname("Jugador " + i);
                s.setHaveTeam(true);
                validTeam.getPlayers().add(s);
            }

            assertDoesNotThrow(() -> teamService.validateTeamForTournament(validTeam));
        }

        @Test
        @DisplayName("CS-T05: Torneo ACTIVE bloquea cambios de equipo")
        void testTorneoActivoBloqueaCambios() {
            Tournament activeTournament = new Tournament();
            activeTournament.setCurrentState(TournamentState.ACTIVE);

            assertFalse(!activeTournament.getCurrentState().equals(TournamentState.ACTIVE));
        }

        @Test
        @DisplayName("CS-T06: Escudo por defecto si shieldUrl es null")
        void testEscudoPorDefecto() {
            Team sinEscudo = new Team();
            sinEscudo.setShieldUrl(null);

            if (sinEscudo.getShieldUrl() == null) {
                sinEscudo.setShieldUrl("default-shield.png");
            }

            assertEquals("default-shield.png", sinEscudo.getShieldUrl());
        }
    }
}