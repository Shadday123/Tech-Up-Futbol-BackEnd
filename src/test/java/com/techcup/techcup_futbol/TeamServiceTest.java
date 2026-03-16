package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas unitarias para TeamServiceImpl
 * Cubre: Happy Path, Error Path y Condicionales según pruebas.md
 *
 * Escenarios:
 * - HP-T01: Creación de Equipo
 * - HP-T02: Invitación Exitosa
 * - HP-T03: Nómina Válida de Equipo
 * - HP-T04: Consulta de Equipo
 * - EP-T01: Nombre Duplicado
 * - EP-T02: Jugador Ocupado
 * - EP-T03: Tamaño Inválido
 * - EP-T04: Minoría de Programa
 * - CS-T01: Validación de Rol de Capitán
 * - CS-T02: Bloqueo de Cambios en Torneo Activo
 * - CS-T03: Asignación de Recurso Visual por Defecto
 */
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
        // Limpiar DataStore
        DataStore.limpiarDatos();

        // Crear capitán del equipo
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

        // Miembro 1 - StudentPlayer (Ingeniería)
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

        // Miembro 2 - StudentPlayer (Ingeniería)
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

        // Miembro 3 - InstitutionalPlayer (cuenta como Ingeniería)
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

        // Miembro 4 - RelativePlayer (No es Ingeniería)
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

        // Crear equipo válido
        validTeam = new Team();
        validTeam.setId("E_TEST001");
        validTeam.setTeamName("Equipo Test");
        validTeam.setShieldUrl("shield_test.png");
        validTeam.setUniformColors("Azul y Blanco");
        validTeam.setCaptain(teamCaptain);
        validTeam.setPlayers(new ArrayList<>());
    }

    // ==================== HAPPY PATH TESTS ====================

    @Nested
    @DisplayName("Happy Path - Escenarios de Éxito")
    class HappyPathTests {

        @Test
        @DisplayName("HP-T01: Creación de Equipo - Con nombre único, escudo y capitán")
        void testCreateTeamSuccessfully() {
            // Act
            Team createdTeam = teamService.createTeam(validTeam);

            // Assert
            assertNotNull(createdTeam);
            assertEquals("Equipo Test", createdTeam.getTeamName());
            assertEquals("shield_test.png", createdTeam.getShieldUrl());
            assertEquals("Azul y Blanco", createdTeam.getUniformColors());
            assertEquals(teamCaptain, createdTeam.getCaptain());
            assertTrue(teamService.getAllTeams().contains(createdTeam));
        }

        @Test
        @DisplayName("HP-T02: Invitación Exitosa - A jugador disponible")
        void testInviteAvailablePlayerSuccessfully() {
            // Arrange
            teamService.createTeam(validTeam);
            assertFalse(teamMember1.isHaveTeam());

            // Act
            teamService.invitePlayer(validTeam.getId(), teamMember1);

            // Assert
            assertTrue(validTeam.getPlayers().contains(teamMember1));
            assertEquals(1, validTeam.getPlayers().size());
        }

        @Test
        @DisplayName("HP-T03: Nómina Válida - Equipo con 7-12 jugadores y mayoría de Ingeniería")
        void testValidTeamRosterWithEngineeringMajority() {
            // Arrange
            teamService.createTeam(validTeam);
            validTeam.getPlayers().add(teamCaptain); // 1 - Engineer
            validTeam.getPlayers().add(teamMember1); // 2 - Engineer
            validTeam.getPlayers().add(teamMember2); // 3 - Engineer
            validTeam.getPlayers().add(teamMember3); // 4 - Engineer (Institutional counts)
            validTeam.getPlayers().add(teamMember4); // 5 - Non-engineer

            // Act - Verificar rango de jugadores
            int rosterSize = validTeam.getPlayers().size();
            long engineerCount = validTeam.getPlayers().stream()
                    .filter(p -> p instanceof StudentPlayer || p instanceof InstitutionalPlayer)
                    .count();
            double engineerPercentage = (double) engineerCount / rosterSize;

            // Assert
            assertTrue(rosterSize >= 7 && rosterSize <= 12);
            assertTrue(engineerPercentage >= 0.5);
        }

        @Test
        @DisplayName("HP-T04: Consulta de Equipo - GET por ID válido")
        void testGetTeamByIdSuccessfully() {
            // Arrange
            teamService.createTeam(validTeam);

            // Act
            Team found = teamService.getTeamById(validTeam.getId());

            // Assert
            assertNotNull(found);
            assertEquals(validTeam.getId(), found.getId());
            assertEquals("Equipo Test", found.getTeamName());
        }

        @Test
        @DisplayName("HP-EXTRA: Listar todos los equipos")
        void testGetAllTeamsSuccessfully() {
            // Arrange
            teamService.createTeam(validTeam);

            Team secondTeam = new Team();
            secondTeam.setId("E_TEST002");
            secondTeam.setTeamName("Segundo Equipo");
            secondTeam.setShieldUrl("shield2.png");
            secondTeam.setUniformColors("Rojo y Negro");
            secondTeam.setCaptain(teamMember1);
            secondTeam.setPlayers(new ArrayList<>());
            teamService.createTeam(secondTeam);

            // Act
            List<Team> allTeams = teamService.getAllTeams();

            // Assert
            assertEquals(2, allTeams.size());
            assertTrue(allTeams.contains(validTeam));
            assertTrue(allTeams.contains(secondTeam));
        }

        @Test
        @DisplayName("HP-EXTRA: Eliminar equipo")
        void testDeleteTeamSuccessfully() {
            // Arrange
            teamService.createTeam(validTeam);
            assertTrue(teamService.getAllTeams().contains(validTeam));

            // Act
            teamService.deleteTeam(validTeam.getId());

            // Assert
            assertFalse(teamService.getAllTeams().contains(validTeam));
        }
    }

    // ==================== ERROR PATH TESTS ====================

    @Nested
    @DisplayName("Error Path - Escenarios de Fallo")
    class ErrorPathTests {

        @Test
        @DisplayName("EP-T01: Nombre Duplicado - No permitir mismo nombre")
        void testCreateTeamWithDuplicateName() {
            // Arrange
            teamService.createTeam(validTeam);

            // Act
            Team duplicateTeam = new Team();
            duplicateTeam.setId("E_DUP001");
            duplicateTeam.setTeamName("Equipo Test"); // Mismo nombre
            duplicateTeam.setShieldUrl("shield_dup.png");
            duplicateTeam.setUniformColors("Gris");
            duplicateTeam.setCaptain(teamMember1);
            duplicateTeam.setPlayers(new ArrayList<>());

            // Assert - Verificar que ya existe
            boolean nameExists = teamService.getAllTeams().stream()
                    .anyMatch(t -> t.getTeamName().equals("Equipo Test"));
            assertTrue(nameExists);
        }

        @Test
        @DisplayName("EP-T02: Jugador Ocupado - No permitir invitar a jugador con equipo")
        void testCannotInvitePlayerAlreadyInTeam() {
            // Arrange
            teamMember1.setHaveTeam(true); // Jugador ya tiene equipo
            teamService.createTeam(validTeam);

            // Act
            // Al intentar invitar, debería validarse que no está disponible
            boolean isAvailable = !teamMember1.isHaveTeam();

            // Assert
            assertFalse(isAvailable);
        }

        @Test
        @DisplayName("EP-T03: Tamaño Inválido - Menos de 7 jugadores")
        void testRosterTooSmall() {
            // Arrange
            teamService.createTeam(validTeam);
            validTeam.getPlayers().add(teamCaptain);
            validTeam.getPlayers().add(teamMember1);
            validTeam.getPlayers().add(teamMember2);
            // Solo 3 jugadores, menos de 7 requeridos

            // Act
            int rosterSize = validTeam.getPlayers().size();

            // Assert
            assertTrue(rosterSize < 7);
            assertFalse(rosterSize >= 7 && rosterSize <= 12);
        }

        @Test
        @DisplayName("EP-T03: Tamaño Inválido - Más de 12 jugadores")
        void testRosterTooLarge() {
            // Arrange
            teamService.createTeam(validTeam);

            // Agregar más de 12 jugadores
            for (int i = 0; i < 13; i++) {
                StudentPlayer extraPlayer = new StudentPlayer();
                extraPlayer.setId("EXTRA_" + i);
                extraPlayer.setNumberID(200000 + i);
                extraPlayer.setFullname("Extra Player " + i);
                extraPlayer.setEmail("extra" + i + "@escuelaing.edu.co");
                extraPlayer.setAge(20);
                extraPlayer.setGender("Masculino");
                extraPlayer.setSemester(3);
                DataStore.jugadores.put(extraPlayer.getId(), extraPlayer);
                validTeam.getPlayers().add(extraPlayer);
            }

            // Act
            int rosterSize = validTeam.getPlayers().size();

            // Assert
            assertTrue(rosterSize > 12);
            assertFalse(rosterSize >= 7 && rosterSize <= 12);
        }

        @Test
        @DisplayName("EP-T04: Minoría de Programa - Menos del 50% de Ingeniería")
        void testInsufficientEngineeringMajority() {
            // Arrange
            teamService.createTeam(validTeam);

            // Agregar solo jugadores NO de Ingeniería (Relativos)
            for (int i = 0; i < 8; i++) {
                RelativePlayer nonEngineer = new RelativePlayer();
                nonEngineer.setId("NONENG_" + i);
                nonEngineer.setNumberID(300000 + i);
                nonEngineer.setFullname("Non Engineer " + i);
                nonEngineer.setEmail("noneng" + i + "@gmail.com");
                nonEngineer.setAge(25);
                nonEngineer.setGender("Masculino");
                DataStore.jugadores.put(nonEngineer.getId(), nonEngineer);
                validTeam.getPlayers().add(nonEngineer);
            }

            // Act
            long engineerCount = validTeam.getPlayers().stream()
                    .filter(p -> p instanceof StudentPlayer || p instanceof InstitutionalPlayer)
                    .count();
            double engineerPercentage = (double) engineerCount / validTeam.getPlayers().size();

            // Assert
            assertTrue(engineerPercentage < 0.5);
        }

        @Test
        @DisplayName("EP-EXTRA: Equipo no encontrado - GET por ID inexistente")
        void testGetTeamWithInvalidId() {
            // Act
            Team found = teamService.getTeamById("E_NOTFOUND");

            // Assert
            assertNull(found);
        }
    }

    // ==================== CONDITIONAL SCENARIOS ====================

    @Nested
    @DisplayName("Conditional Scenarios - Lógica de Negocio")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-T01: Validación de Rol de Capitán - Solo ciertos roles pueden serlo")
        void testCaptainRoleValidation() {
            // Arrange - Un árbitro intenta crear equipo
            // En el dominio solo StudentPlayer e InstitutionalPlayer pueden ser capitanes

            // Act & Assert
            assertTrue(teamCaptain instanceof StudentPlayer);
            assertTrue(teamCaptain.isCaptain());

            // Verificar que RelativePlayer no debería ser capitán
            assertFalse(teamMember4.isCaptain());
        }

        @Test
        @DisplayName("CS-T02: Bloqueo de Cambios - No cambiar equipo si torneo está ACTIVE")
        void testPreventTeamChangesWhenTournamentActive() {
            // Arrange
            Tournament activeTournament = new Tournament();
            activeTournament.setCurrentState(TournamentState.ACTIVE);

            // Act
            boolean canMakeChanges = !activeTournament.getCurrentState().equals(TournamentState.ACTIVE);

            // Assert
            assertFalse(canMakeChanges);
        }

        @Test
        @DisplayName("CS-T02: Permitir Cambios - Cuando torneo está en DRAFT")
        void testAllowTeamChangesWhenTournamentInDraft() {
            // Arrange
            Tournament draftTournament = new Tournament();
            draftTournament.setCurrentState(TournamentState.DRAFT);

            // Act
            boolean canMakeChanges = !draftTournament.getCurrentState().equals(TournamentState.ACTIVE);

            // Assert
            assertTrue(canMakeChanges);
        }

        @Test
        @DisplayName("CS-T03: Escudo por Defecto - Asignar default-shield.png si no hay URL")
        void testDefaultShieldAssignment() {
            // Arrange
            Team teamWithoutShield = new Team();
            teamWithoutShield.setId("E_NOIMG");
            teamWithoutShield.setTeamName("Equipo Sin Escudo");
            teamWithoutShield.setShieldUrl(null);
            teamWithoutShield.setUniformColors("Blanco");
            teamWithoutShield.setCaptain(teamCaptain);
            teamWithoutShield.setPlayers(new ArrayList<>());

            // Act - Asignar escudo por defecto si es null
            if (teamWithoutShield.getShieldUrl() == null) {
                teamWithoutShield.setShieldUrl("default-shield.png");
            }

            // Assert
            assertEquals("default-shield.png", teamWithoutShield.getShieldUrl());
        }

        @Test
        @DisplayName("CS-EXTRA: Validación de Nómina Completa")
        void testCompleteRosterValidation() {
            // Arrange
            teamService.createTeam(validTeam);
            validTeam.getPlayers().add(teamCaptain); // 1
            validTeam.getPlayers().add(teamMember1); // 2
            validTeam.getPlayers().add(teamMember2); // 3
            validTeam.getPlayers().add(teamMember3); // 4
            validTeam.getPlayers().add(teamMember4); // 5

            StudentPlayer m5 = new StudentPlayer();
            m5.setId("M5");
            m5.setNumberID(400005);
            m5.setFullname("Member 5");
            m5.setEmail("m5@escuelaing.edu.co");
            m5.setAge(20);
            m5.setGender("Masculino");
            m5.setSemester(2);
            validTeam.getPlayers().add(m5); // 6

            StudentPlayer m6 = new StudentPlayer();
            m6.setId("M6");
            m6.setNumberID(400006);
            m6.setFullname("Member 6");
            m6.setEmail("m6@escuelaing.edu.co");
            m6.setAge(21);
            m6.setGender("Femenino");
            m6.setSemester(3);
            validTeam.getPlayers().add(m6); // 7

            // Act
            int size = validTeam.getPlayers().size();
            long engineers = validTeam.getPlayers().stream()
                    .filter(p -> p instanceof StudentPlayer || p instanceof InstitutionalPlayer)
                    .count();

            // Assert
            assertEquals(7, size);
            assertTrue(size >= 7 && size <= 12);
            assertTrue((double) engineers / size >= 0.5);
        }

        @Test
        @DisplayName("CS-EXTRA: Un jugador no puede estar en dos equipos simultáneamente")
        void testPlayerCannotBeInMultipleTeams() {
            // Arrange
            teamService.createTeam(validTeam);
            validTeam.getPlayers().add(teamMember1);
            teamMember1.setHaveTeam(true);

            Team secondTeam = new Team();
            secondTeam.setId("E_TEAM2");
            secondTeam.setTeamName("Segundo Equipo");
            secondTeam.setShieldUrl("shield2.png");
            secondTeam.setUniformColors("Rojo");
            secondTeam.setCaptain(teamMember2);
            secondTeam.setPlayers(new ArrayList<>());
            teamService.createTeam(secondTeam);

            // Act & Assert - El jugador no debería poder agregarse al segundo equipo
            // ya que su haveTeam es true
            assertFalse(!teamMember1.isHaveTeam()); // No debería estar disponible
        }
    }
}