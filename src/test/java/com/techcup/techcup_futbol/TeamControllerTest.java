package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.TeamController;
import com.techcup.techcup_futbol.Controller.dto.CreateTeamRequest;
import com.techcup.techcup_futbol.Controller.dto.TeamResponse;
import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.PlayerService;
import com.techcup.techcup_futbol.core.service.TeamService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamController Tests")
class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @Mock
    private PlayerService playerService;

    private TeamController controller;

    @BeforeEach
    void setUp() {
        controller = new TeamController(teamService, playerService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TC-01: create() retorna 201 CREATED con el equipo creado")
        void createRetorna201() {
            CreateTeamRequest req = buildRequest("Equipo HP", "cap-id-001");
            StudentPlayer capitan = buildPlayer("cap@escuelaing.edu.co", "Capitan");
            Team savedTeam = buildTeam("Equipo HP", capitan);

            when(playerService.obtenerPorId("cap-id-001")).thenReturn(capitan);
            when(teamService.createTeam(any(Team.class))).thenReturn(savedTeam);

            ResponseEntity<TeamResponse> response = controller.create(req);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("HP-TC-02: create() llama al servicio con el equipo construido")
        void createLlamaServicio() {
            CreateTeamRequest req = buildRequest("Equipo HP2", "cap-id-002");
            StudentPlayer capitan = buildPlayer("cap2@escuelaing.edu.co", "Capitan2");
            Team savedTeam = buildTeam("Equipo HP2", capitan);

            when(playerService.obtenerPorId("cap-id-002")).thenReturn(capitan);
            when(teamService.createTeam(any(Team.class))).thenReturn(savedTeam);

            controller.create(req);

            verify(teamService, times(1)).createTeam(any(Team.class));
        }

        @Test
        @DisplayName("HP-TC-03: findAll() retorna 200 OK con lista de equipos")
        void findAllRetorna200() {
            List<Team> equipos = List.of(
                    buildTeam("A", buildPlayer("a@escuelaing.edu.co", "A")),
                    buildTeam("B", buildPlayer("b@escuelaing.edu.co", "B"))
            );
            when(teamService.getAllTeams()).thenReturn(equipos);

            ResponseEntity<List<TeamResponse>> response = controller.findAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }

        @Test
        @DisplayName("HP-TC-04: findById() retorna 200 OK cuando equipo existe")
        void findByIdRetorna200() {
            Team team = buildTeam("Found", buildPlayer("found@escuelaing.edu.co", "Found"));
            when(teamService.buscarPorId(team.getId())).thenReturn(Optional.of(team));

            ResponseEntity<TeamResponse> response = controller.findById(team.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-TC-05: findById() retorna 404 cuando equipo no existe")
        void findByIdRetorna404() {
            when(teamService.buscarPorId("NO-EXISTE")).thenReturn(Optional.empty());

            ResponseEntity<TeamResponse> response = controller.findById("NO-EXISTE");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-TC-06: delete() retorna 204 NO CONTENT")
        void deleteRetorna204() {
            doNothing().when(teamService).deleteTeam("TEAM-001");

            ResponseEntity<Void> response = controller.delete("TEAM-001");

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(teamService, times(1)).deleteTeam("TEAM-001");
        }

        @Test
        @DisplayName("HP-TC-07: invitePlayer() retorna 200 OK")
        void invitePlayerRetorna200() {
            StudentPlayer jugador = buildPlayer("inv@escuelaing.edu.co", "Invitado");
            when(playerService.obtenerPorId(jugador.getId())).thenReturn(jugador);
            doNothing().when(teamService).invitePlayer("TEAM-001", jugador);

            ResponseEntity<Void> response = controller.invitePlayer("TEAM-001", jugador.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-TC-08: removePlayer() retorna 204 NO CONTENT")
        void removePlayerRetorna204() {
            doNothing().when(teamService).removePlayer("TEAM-001", "PLAYER-001");

            ResponseEntity<Void> response = controller.removePlayer("TEAM-001", "PLAYER-001");

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(teamService, times(1)).removePlayer("TEAM-001", "PLAYER-001");
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TC-01: create() propaga TeamException si nombre duplicado")
        void createPropagaExcepcionNombreDuplicado() {
            CreateTeamRequest req = buildRequest("Duplicado", "cap-dup");
            when(playerService.obtenerPorId("cap-dup")).thenReturn(
                    buildPlayer("dup@escuelaing.edu.co", "Dup Cap"));
            doThrow(new TeamException("teamName", TeamException.TEAM_NAME_ALREADY_EXISTS.formatted("Duplicado")))
                    .when(teamService).createTeam(any(Team.class));

            assertThrows(TeamException.class, () -> controller.create(req));
        }

        @Test
        @DisplayName("EP-TC-02: create() propaga PlayerException si capitán no existe")
        void createPropagaExcepcionCapitanNoExiste() {
            CreateTeamRequest req = buildRequest("Sin Cap", "cap-no-existe");
            doThrow(new com.techcup.techcup_futbol.core.exception.PlayerException(
                    "id", "No existe jugador con ID: cap-no-existe"))
                    .when(playerService).obtenerPorId("cap-no-existe");

            assertThrows(com.techcup.techcup_futbol.core.exception.PlayerException.class,
                    () -> controller.create(req));
        }

        @Test
        @DisplayName("EP-TC-03: delete() propaga TeamException si equipo no existe")
        void deletePropagaExcepcion() {
            doThrow(new TeamException("id", TeamException.TEAM_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(teamService).deleteTeam("NO-EXISTE");

            assertThrows(TeamException.class, () -> controller.delete("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-TC-04: invitePlayer() propaga TeamException si jugador ya tiene equipo")
        void invitePlayerPropagaExcepcion() {
            StudentPlayer jugador = buildPlayer("ocupado@escuelaing.edu.co", "Ocupado");
            when(playerService.obtenerPorId(jugador.getId())).thenReturn(jugador);
            doThrow(new TeamException("player",
                    TeamException.PLAYER_ALREADY_HAS_TEAM.formatted(jugador.getFullname())))
                    .when(teamService).invitePlayer("TEAM-001", jugador);

            assertThrows(TeamException.class,
                    () -> controller.invitePlayer("TEAM-001", jugador.getId()));
        }

        @Test
        @DisplayName("EP-TC-05: removePlayer() propaga TeamException si jugador no está en equipo")
        void removePlayerPropagaExcepcion() {
            doThrow(new TeamException("player",
                    TeamException.PLAYER_NOT_IN_TEAM.formatted("P-001", "TEAM-001")))
                    .when(teamService).removePlayer("TEAM-001", "P-001");

            assertThrows(TeamException.class,
                    () -> controller.removePlayer("TEAM-001", "P-001"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TC-01: findAll() retorna lista vacía con 200 OK si no hay equipos")
        void findAllRetornaVacioConOk() {
            when(teamService.getAllTeams()).thenReturn(List.of());

            ResponseEntity<List<TeamResponse>> response = controller.findAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("CS-TC-02: create() obtiene capitán del PlayerService antes de crear equipo")
        void createObtieneCaptainDePlayerService() {
            CreateTeamRequest req = buildRequest("Equipo CS", "cap-cs");
            StudentPlayer capitan = buildPlayer("cs@escuelaing.edu.co", "CS Cap");
            Team savedTeam = buildTeam("Equipo CS", capitan);

            when(playerService.obtenerPorId("cap-cs")).thenReturn(capitan);
            when(teamService.createTeam(any(Team.class))).thenReturn(savedTeam);

            controller.create(req);

            verify(playerService, times(1)).obtenerPorId("cap-cs");
        }

        @Test
        @DisplayName("CS-TC-03: invitePlayer() obtiene el jugador del PlayerService antes de invitar")
        void invitePlayerObtienJugador() {
            StudentPlayer jugador = buildPlayer("inv2@escuelaing.edu.co", "Invitado2");
            when(playerService.obtenerPorId(jugador.getId())).thenReturn(jugador);
            doNothing().when(teamService).invitePlayer("TEAM-002", jugador);

            controller.invitePlayer("TEAM-002", jugador.getId());

            verify(playerService, times(1)).obtenerPorId(jugador.getId());
            verify(teamService, times(1)).invitePlayer("TEAM-002", jugador);
        }

        @Test
        @DisplayName("CS-TC-04: create() sin captainId no consulta al PlayerService")
        void createSinCaptainNoConsultaPlayerService() {
            CreateTeamRequest req = buildRequest("Sin Captain", null);
            Team savedTeam = buildTeam("Sin Captain", null);
            when(teamService.createTeam(any(Team.class))).thenReturn(savedTeam);

            controller.create(req);

            verify(playerService, never()).obtenerPorId(anyString());
        }

        @Test
        @DisplayName("CS-TC-05: findById() llama al servicio exactamente una vez")
        void findByIdLlamaServicioUnaVez() {
            when(teamService.buscarPorId("T-CHECK")).thenReturn(Optional.empty());

            controller.findById("T-CHECK");

            verify(teamService, times(1)).buscarPorId("T-CHECK");
        }
    }

    // ── Helpers

    private CreateTeamRequest buildRequest(String name, String captainId) {
        CreateTeamRequest req = new CreateTeamRequest();
        req.setTeamName(name);
        req.setShieldUrl("shield.png");
        req.setUniformColors("Rojo");
        req.setCaptainId(captainId);
        return req;
    }

    private StudentPlayer buildPlayer(String email, String name) {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname(name);
        p.setEmail(email);
        p.setNumberID(999990);
        p.setAge(22);
        p.setGender("Masculino");
        p.setSemester(4);
        p.setDorsalNumber(8);
        p.setPosition(PositionEnum.GoalKeeper);
        p.setHaveTeam(false);
        return p;
    }

    private Team buildTeam(String name, StudentPlayer captain) {
        Team team = new Team();
        team.setId(UUID.randomUUID().toString());
        team.setTeamName(name);
        team.setShieldUrl("shield.png");
        team.setUniformColors("Azul");
        team.setCaptain(captain);
        team.setPlayers(new ArrayList<>());
        return team;
    }
}
