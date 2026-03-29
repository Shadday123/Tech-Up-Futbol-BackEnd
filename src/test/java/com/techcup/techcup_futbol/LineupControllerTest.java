package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.LineupController;
import com.techcup.techcup_futbol.Controller.dto.*;
import com.techcup.techcup_futbol.core.exception.LineupException;
import com.techcup.techcup_futbol.core.service.LineupService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LineupController Tests")
class LineupControllerTest {

    @Mock
    private LineupService lineupService;

    private LineupController controller;

    @BeforeEach
    void setUp() {
        controller = new LineupController(lineupService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-LC-01: create() retorna 201 CREATED con la alineación creada")
        void createRetorna201() {
            CreateLineupRequest req = buildRequest("M001", "E001");
            LineupResponse resp = buildResponse("LIN-001", "M001", "E001");
            when(lineupService.create(req)).thenReturn(resp);

            ResponseEntity<LineupResponse> response = controller.create(req);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("LIN-001", response.getBody().id());
        }

        @Test
        @DisplayName("HP-LC-02: create() llama al servicio exactamente una vez")
        void createLlamaServicio() {
            CreateLineupRequest req = buildRequest("M001", "E001");
            when(lineupService.create(req)).thenReturn(buildResponse("LIN-001", "M001", "E001"));

            controller.create(req);
            verify(lineupService, times(1)).create(req);
        }

        @Test
        @DisplayName("HP-LC-03: findByMatchAndTeam() retorna 200 OK con la alineación")
        void findByMatchAndTeamRetorna200() {
            LineupResponse resp = buildResponse("LIN-001", "M001", "E001");
            when(lineupService.findByMatchAndTeam("M001", "E001")).thenReturn(resp);

            ResponseEntity<LineupResponse> response = controller.findByMatchAndTeam("M001", "E001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("M001", response.getBody().matchId());
        }

        @Test
        @DisplayName("HP-LC-04: findRivalLineup() retorna 200 OK con la alineación rival")
        void findRivalLineupRetorna200() {
            LineupResponse resp = buildResponse("LIN-RIVAL", "M001", "E002");
            when(lineupService.findRivalLineup("M001", "E001")).thenReturn(resp);

            ResponseEntity<LineupResponse> response = controller.findRivalLineup("M001", "E001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-LC-01: create() propaga LineupException si partido no existe")
        void createPropagaExcepcionPartidoNoExiste() {
            CreateLineupRequest req = buildRequest("NO-EXISTE", "E001");
            doThrow(new LineupException("matchId",
                    LineupException.MATCH_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(lineupService).create(req);

            assertThrows(LineupException.class, () -> controller.create(req));
        }

        @Test
        @DisplayName("EP-LC-02: create() propaga LineupException si alineación ya existe")
        void createPropagaExcepcionAlineacionDuplicada() {
            CreateLineupRequest req = buildRequest("M001", "E001");
            doThrow(new LineupException("lineup",
                    LineupException.LINEUP_ALREADY_EXISTS.formatted("M001", "Equipo A")))
                    .when(lineupService).create(req);

            assertThrows(LineupException.class, () -> controller.create(req));
        }

        @Test
        @DisplayName("EP-LC-03: create() propaga LineupException si número de titulares incorrecto")
        void createPropagaExcepcionTitularesIncorrectos() {
            CreateLineupRequest req = buildRequest("M001", "E001");
            doThrow(new LineupException("starters",
                    LineupException.WRONG_STARTERS_COUNT.formatted(5)))
                    .when(lineupService).create(req);

            LineupException ex = assertThrows(LineupException.class,
                    () -> controller.create(req));
            assertEquals("starters", ex.getField());
        }

        @Test
        @DisplayName("EP-LC-04: findByMatchAndTeam() propaga LineupException si no existe")
        void findByMatchAndTeamPropagaExcepcion() {
            doThrow(new LineupException("lineup",
                    LineupException.LINEUP_NOT_FOUND.formatted("M001", "E001")))
                    .when(lineupService).findByMatchAndTeam("M001", "E001");

            assertThrows(LineupException.class,
                    () -> controller.findByMatchAndTeam("M001", "E001"));
        }

        @Test
        @DisplayName("EP-LC-05: findRivalLineup() propaga LineupException si rival no publicó")
        void findRivalLineupPropagaExcepcion() {
            doThrow(new LineupException("lineup", LineupException.RIVAL_LINEUP_NOT_PUBLISHED))
                    .when(lineupService).findRivalLineup("M001", "E001");

            assertThrows(LineupException.class,
                    () -> controller.findRivalLineup("M001", "E001"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-LC-01: findByMatchAndTeam() llama al servicio con matchId y teamId correctos")
        void findByMatchAndTeamLlamaServicio() {
            when(lineupService.findByMatchAndTeam("M-X", "E-Y"))
                    .thenReturn(buildResponse("LIN-X", "M-X", "E-Y"));

            controller.findByMatchAndTeam("M-X", "E-Y");
            verify(lineupService, times(1)).findByMatchAndTeam("M-X", "E-Y");
        }

        @Test
        @DisplayName("CS-LC-02: findRivalLineup() llama al servicio con matchId y myTeamId correctos")
        void findRivalLineupLlamaServicio() {
            when(lineupService.findRivalLineup("M-X", "E-Y"))
                    .thenReturn(buildResponse("LIN-RIVAL", "M-X", "E-Z"));

            controller.findRivalLineup("M-X", "E-Y");
            verify(lineupService, times(1)).findRivalLineup("M-X", "E-Y");
        }

        @Test
        @DisplayName("CS-LC-03: create() retorna el body exacto que devuelve el servicio")
        void createRetornaBodyDelServicio() {
            CreateLineupRequest req = buildRequest("M001", "E001");
            LineupResponse esperado = buildResponse("LIN-BODY", "M001", "E001");
            when(lineupService.create(req)).thenReturn(esperado);

            ResponseEntity<LineupResponse> response = controller.create(req);
            assertSame(esperado, response.getBody());
        }
    }

    // ── Helpers

    private CreateLineupRequest buildRequest(String matchId, String teamId) {
        List<String> starters = List.of(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        return new CreateLineupRequest(matchId, teamId, "4-3-3", starters, List.of(), List.of());
    }

    private LineupResponse buildResponse(String id, String matchId, String teamId) {
        return new LineupResponse(id, matchId, teamId, "Equipo " + teamId, "4-3-3",
                List.of(), List.of(), List.of());
    }
}
