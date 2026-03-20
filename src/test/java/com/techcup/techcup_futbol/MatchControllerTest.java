package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.MatchController;
import com.techcup.techcup_futbol.Controller.dto.MatchDTOs.*;
import com.techcup.techcup_futbol.core.exception.MatchException;
import com.techcup.techcup_futbol.core.service.MatchService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchController Tests")
class MatchControllerTest {

    @Mock
    private MatchService matchService;

    private MatchController controller;

    @BeforeEach
    void setUp() {
        controller = new MatchController(matchService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-MC-01: create() retorna 201 CREATED con el partido creado")
        void createRetorna201() {
            CreateMatchRequest req = buildCreateRequest("E001", "E002");
            MatchResponse resp = buildMatchResponse("M001", "SCHEDULED");
            when(matchService.create(req)).thenReturn(resp);

            ResponseEntity<MatchResponse> response = controller.create(req);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("M001", response.getBody().id());
        }

        @Test
        @DisplayName("HP-MC-02: registerResult() retorna 200 OK con resultado registrado")
        void registerResultRetorna200() {
            RegisterResultRequest req = new RegisterResultRequest(2, 1, List.of());
            MatchResponse resp = buildMatchResponse("M001", "FINISHED");
            when(matchService.registerResult("M001", req)).thenReturn(resp);

            ResponseEntity<MatchResponse> response = controller.registerResult("M001", req);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("FINISHED", response.getBody().status());
        }

        @Test
        @DisplayName("HP-MC-03: findAll() retorna 200 OK con todos los partidos")
        void findAllRetorna200() {
            when(matchService.findAll()).thenReturn(List.of(
                    buildMatchResponse("M001", "SCHEDULED"),
                    buildMatchResponse("M002", "FINISHED")
            ));

            ResponseEntity<List<MatchResponse>> response = controller.findAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }

        @Test
        @DisplayName("HP-MC-04: findById() retorna 200 OK con el partido encontrado")
        void findByIdRetorna200() {
            MatchResponse resp = buildMatchResponse("M001", "SCHEDULED");
            when(matchService.findById("M001")).thenReturn(resp);

            ResponseEntity<MatchResponse> response = controller.findById("M001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("M001", response.getBody().id());
        }

        @Test
        @DisplayName("HP-MC-05: findByTeam() retorna 200 OK con partidos del equipo")
        void findByTeamRetorna200() {
            when(matchService.findByTeamId("E001")).thenReturn(List.of(
                    buildMatchResponse("M001", "SCHEDULED")
            ));

            ResponseEntity<List<MatchResponse>> response = controller.findByTeam("E001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-MC-01: create() propaga MatchException si equipo local no existe")
        void createPropagaExcepcionEquipoNoExiste() {
            CreateMatchRequest req = buildCreateRequest("NO-EXISTE", "E002");
            doThrow(new MatchException("localTeamId",
                    MatchException.TEAM_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(matchService).create(req);

            assertThrows(MatchException.class, () -> controller.create(req));
        }

        @Test
        @DisplayName("EP-MC-02: create() propaga MatchException si ambos equipos son iguales")
        void createPropagaExcepcionMismoEquipo() {
            CreateMatchRequest req = buildCreateRequest("E001", "E001");
            doThrow(new MatchException("teams", MatchException.SAME_TEAM))
                    .when(matchService).create(req);

            MatchException ex = assertThrows(MatchException.class,
                    () -> controller.create(req));
            assertEquals("teams", ex.getField());
        }

        @Test
        @DisplayName("EP-MC-03: registerResult() propaga MatchException si partido no existe")
        void registerResultPropagaExcepcion() {
            RegisterResultRequest req = new RegisterResultRequest(1, 0, List.of());
            doThrow(new MatchException("matchId",
                    MatchException.MATCH_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(matchService).registerResult("NO-EXISTE", req);

            assertThrows(MatchException.class,
                    () -> controller.registerResult("NO-EXISTE", req));
        }

        @Test
        @DisplayName("EP-MC-04: registerResult() propaga MatchException si resultado ya registrado")
        void registerResultYaRegistradoPropagaExcepcion() {
            RegisterResultRequest req = new RegisterResultRequest(1, 0, List.of());
            doThrow(new MatchException("status", MatchException.RESULT_ALREADY_REGISTERED))
                    .when(matchService).registerResult("M001", req);

            assertThrows(MatchException.class,
                    () -> controller.registerResult("M001", req));
        }

        @Test
        @DisplayName("EP-MC-05: findById() propaga MatchException si partido no existe")
        void findByIdPropagaExcepcion() {
            doThrow(new MatchException("matchId",
                    MatchException.MATCH_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(matchService).findById("NO-EXISTE");

            assertThrows(MatchException.class, () -> controller.findById("NO-EXISTE"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-MC-01: findAll() retorna lista vacía con 200 OK si no hay partidos")
        void findAllVacioConOk() {
            when(matchService.findAll()).thenReturn(List.of());

            ResponseEntity<List<MatchResponse>> response = controller.findAll();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("CS-MC-02: create() llama al servicio exactamente una vez")
        void createLlamaServicioUnaVez() {
            CreateMatchRequest req = buildCreateRequest("E001", "E002");
            when(matchService.create(req)).thenReturn(buildMatchResponse("M001", "SCHEDULED"));

            controller.create(req);
            verify(matchService, times(1)).create(req);
        }

        @Test
        @DisplayName("CS-MC-03: findByTeam() retorna lista vacía si equipo no tiene partidos")
        void findByTeamSinPartidos() {
            when(matchService.findByTeamId("E-SIN-PARTIDOS")).thenReturn(List.of());

            ResponseEntity<List<MatchResponse>> response = controller.findByTeam("E-SIN-PARTIDOS");
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }
    }

    // ── Helpers

    private CreateMatchRequest buildCreateRequest(String localId, String visitorId) {
        return new CreateMatchRequest(localId, visitorId, LocalDateTime.now().plusDays(5), null, 1);
    }

    private MatchResponse buildMatchResponse(String id, String status) {
        return new MatchResponse(id, "E001", "Local FC", "E002", "Visitor FC",
                LocalDateTime.now().plusDays(5), 0, 0, 0, 0, 1, status, List.of());
    }
}
