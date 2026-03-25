package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.TournamentController;
import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.service.TournamentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TournamentController Tests")
class TournamentControllerTest {

    @Mock
    private TournamentService tournamentService;

    private TournamentController controller;

    private static final LocalDateTime START = LocalDateTime.now().plusDays(5);
    private static final LocalDateTime END   = LocalDateTime.now().plusDays(30);

    @BeforeEach
    void setUp() {
        controller = new TournamentController(tournamentService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TOC-01: create() retorna 201 CREATED con el torneo creado")
        void createRetorna201() {
            CreateTournamentRequest req = buildRequest("Torneo HP");
            TournamentResponse resp = buildResponse("T001", "Torneo HP", "DRAFT");
            when(tournamentService.create(req)).thenReturn(resp);

            ResponseEntity<TournamentResponse> response = controller.create(req);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("T001", response.getBody().id());
        }

        @Test
        @DisplayName("HP-TOC-02: create() llama al servicio exactamente una vez")
        void createLlamaServicio() {
            CreateTournamentRequest req = buildRequest("Torneo HP2");
            when(tournamentService.create(req)).thenReturn(buildResponse("T002", "Torneo HP2", "DRAFT"));

            controller.create(req);

            verify(tournamentService, times(1)).create(req);
        }

        @Test
        @DisplayName("HP-TOC-03: findAll() retorna 200 OK con lista de torneos")
        void findAllRetorna200() {
            List<TournamentResponse> torneos = List.of(
                    buildResponse("T001", "T1", "DRAFT"),
                    buildResponse("T002", "T2", "ACTIVE")
            );
            when(tournamentService.findAll()).thenReturn(torneos);

            ResponseEntity<List<TournamentResponse>> response = controller.findAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }

        @Test
        @DisplayName("HP-TOC-04: findById() retorna 200 OK con el torneo encontrado")
        void findByIdRetorna200() {
            TournamentResponse resp = buildResponse("T001", "Torneo Find", "DRAFT");
            when(tournamentService.findById("T001")).thenReturn(resp);

            ResponseEntity<TournamentResponse> response = controller.findById("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Torneo Find", response.getBody().name());
        }

        @Test
        @DisplayName("HP-TOC-05: start() retorna 200 OK con estado ACTIVE")
        void startRetorna200() {
            TournamentResponse resp = buildResponse("T001", "Torneo Start", "ACTIVE");
            when(tournamentService.updateStatus("T001", "ACTIVE")).thenReturn(resp);

            ResponseEntity<TournamentResponse> response = controller.start("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("ACTIVE", response.getBody().currentState());
        }

        @Test
        @DisplayName("HP-TOC-06: progress() retorna 200 OK con estado IN_PROGRESS")
        void progressRetorna200() {
            TournamentResponse resp = buildResponse("T001", "Torneo Progress", "IN_PROGRESS");
            when(tournamentService.updateStatus("T001", "IN_PROGRESS")).thenReturn(resp);

            ResponseEntity<TournamentResponse> response = controller.progress("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("IN_PROGRESS", response.getBody().currentState());
        }

        @Test
        @DisplayName("HP-TOC-07: finish() retorna 200 OK con estado COMPLETED")
        void finishRetorna200() {
            TournamentResponse resp = buildResponse("T001", "Torneo Finish", "COMPLETED");
            when(tournamentService.updateStatus("T001", "COMPLETED")).thenReturn(resp);

            ResponseEntity<TournamentResponse> response = controller.finish("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("COMPLETED", response.getBody().currentState());
        }

        @Test
        @DisplayName("HP-TOC-08: softDelete() retorna 200 OK con estado DELETED")
        void softDeleteRetorna200() {
            TournamentResponse resp = buildResponse("T001", "Torneo Deleted", "DELETED");
            when(tournamentService.updateStatus("T001", "DELETED")).thenReturn(resp);

            ResponseEntity<TournamentResponse> response = controller.softDelete("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("DELETED", response.getBody().currentState());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TOC-01: create() propaga TournamentException si request inválido")
        void createPropagaExcepcion() {
            CreateTournamentRequest req = buildRequest("");
            doThrow(new TournamentException("name", TournamentException.NAME_EMPTY))
                    .when(tournamentService).create(req);

            assertThrows(TournamentException.class, () -> controller.create(req));
        }

        @Test
        @DisplayName("EP-TOC-02: findById() propaga TournamentException si torneo no existe")
        void findByIdPropagaExcepcion() {
            doThrow(new TournamentException("id",
                    TournamentException.TOURNAMENT_NOT_FOUND.formatted("T999")))
                    .when(tournamentService).findById("T999");

            TournamentException ex = assertThrows(TournamentException.class,
                    () -> controller.findById("T999"));
            assertEquals("id", ex.getField());
        }

        @Test
        @DisplayName("EP-TOC-03: start() propaga TournamentException si torneo no existe")
        void startPropagaExcepcionTorneoNoExiste() {
            doThrow(new TournamentException("id",
                    TournamentException.TOURNAMENT_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(tournamentService).updateStatus("NO-EXISTE", "ACTIVE");

            assertThrows(TournamentException.class, () -> controller.start("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-TOC-04: progress() propaga TournamentException si transición inválida")
        void progressPropagaExcepcionTransicionInvalida() {
            doThrow(new TournamentException("state",
                    TournamentException.INVALID_STATE_TRANSITION.formatted("DRAFT", "IN_PROGRESS")))
                    .when(tournamentService).updateStatus("T001", "IN_PROGRESS");

            TournamentException ex = assertThrows(TournamentException.class,
                    () -> controller.progress("T001"));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TOC-05: finish() propaga TournamentException si torneo ya está COMPLETED")
        void finishPropagaExcepcionYaCompleted() {
            doThrow(new TournamentException("state",
                    TournamentException.INVALID_STATE_TRANSITION.formatted("COMPLETED", "COMPLETED")))
                    .when(tournamentService).updateStatus("T001", "COMPLETED");

            assertThrows(TournamentException.class, () -> controller.finish("T001"));
        }

        @Test
        @DisplayName("EP-TOC-06: softDelete() propaga TournamentException si torneo ya está DELETED")
        void softDeletePropagaExcepcionYaDeleted() {
            doThrow(new TournamentException("state",
                    TournamentException.INVALID_STATE_TRANSITION.formatted("DELETED", "DELETED")))
                    .when(tournamentService).updateStatus("T001", "DELETED");

            assertThrows(TournamentException.class, () -> controller.softDelete("T001"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TOC-01: findAll() retorna lista vacía con 200 OK si no hay torneos")
        void findAllRetornaVacioConOk() {
            when(tournamentService.findAll()).thenReturn(List.of());

            ResponseEntity<List<TournamentResponse>> response = controller.findAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("CS-TOC-02: start() pasa exactamente 'ACTIVE' al servicio")
        void startPasaActivAlServicio() {
            when(tournamentService.updateStatus("T001", "ACTIVE"))
                    .thenReturn(buildResponse("T001", "T", "ACTIVE"));

            controller.start("T001");

            verify(tournamentService).updateStatus("T001", "ACTIVE");
        }

        @Test
        @DisplayName("CS-TOC-03: progress() pasa exactamente 'IN_PROGRESS' al servicio")
        void progressPasaInProgressAlServicio() {
            when(tournamentService.updateStatus("T001", "IN_PROGRESS"))
                    .thenReturn(buildResponse("T001", "T", "IN_PROGRESS"));

            controller.progress("T001");

            verify(tournamentService).updateStatus("T001", "IN_PROGRESS");
        }

        @Test
        @DisplayName("CS-TOC-04: finish() pasa exactamente 'COMPLETED' al servicio")
        void finishPasaCompletedAlServicio() {
            when(tournamentService.updateStatus("T001", "COMPLETED"))
                    .thenReturn(buildResponse("T001", "T", "COMPLETED"));

            controller.finish("T001");

            verify(tournamentService).updateStatus("T001", "COMPLETED");
        }

        @Test
        @DisplayName("CS-TOC-05: softDelete() pasa exactamente 'DELETED' al servicio")
        void softDeletePasaDeletedAlServicio() {
            when(tournamentService.updateStatus("T001", "DELETED"))
                    .thenReturn(buildResponse("T001", "T", "DELETED"));

            controller.softDelete("T001");

            verify(tournamentService).updateStatus("T001", "DELETED");
        }

        @Test
        @DisplayName("CS-TOC-06: create() retorna el body exacto que devuelve el servicio")
        void createRetornaBodyDelServicio() {
            CreateTournamentRequest req = buildRequest("Torneo Exacto");
            TournamentResponse esperado = buildResponse("T005", "Torneo Exacto", "DRAFT");
            when(tournamentService.create(req)).thenReturn(esperado);

            ResponseEntity<TournamentResponse> response = controller.create(req);

            assertSame(esperado, response.getBody());
        }
    }

    // ── Helpers

    private CreateTournamentRequest buildRequest(String name) {
        return new CreateTournamentRequest(name, START, END, 150.0, 8, "Reglas estándar");
    }

    private TournamentResponse buildResponse(String id, String name, String state) {
        return new TournamentResponse(id, name, START, END, 150.0, 8, "Reglas estándar", state);
    }
}
