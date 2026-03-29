package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.RefereeController;
import com.techcup.techcup_futbol.Controller.dto.*;
import com.techcup.techcup_futbol.core.exception.RefereeException;
import com.techcup.techcup_futbol.core.service.RefereeService;
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
@DisplayName("RefereeController Tests")
class RefereeControllerTest {

    @Mock
    private RefereeService refereeService;

    private RefereeController controller;

    @BeforeEach
    void setUp() {
        controller = new RefereeController(refereeService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-RC-01: create() retorna 201 CREATED con el árbitro creado")
        void createRetorna201() {
            CreateRefereeRequest req = new CreateRefereeRequest("Árbitro Test", "arbitro@gmail.com");
            RefereeResponse resp = buildResponse("arbitro@gmail.com", "Árbitro Test");
            when(refereeService.create(req)).thenReturn(resp);

            ResponseEntity<RefereeResponse> response = controller.create(req);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("HP-RC-02: create() llama al servicio exactamente una vez")
        void createLlamaServicio() {
            CreateRefereeRequest req = new CreateRefereeRequest("Árbitro 2", "arb2@gmail.com");
            when(refereeService.create(req)).thenReturn(buildResponse("arb2@gmail.com", "Árbitro 2"));

            controller.create(req);

            verify(refereeService, times(1)).create(req);
        }

        @Test
        @DisplayName("HP-RC-03: assignToMatch() retorna 200 OK con árbitro asignado")
        void assignToMatchRetorna200() {
            AssignRefereeRequest req = new AssignRefereeRequest("REF-001");
            RefereeResponse resp = buildResponse("arb@gmail.com", "Árbitro Asignado");
            when(refereeService.assignToMatch("MATCH-001", req)).thenReturn(resp);

            ResponseEntity<RefereeResponse> response = controller.assignToMatch("MATCH-001", req);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(refereeService, times(1)).assignToMatch("MATCH-001", req);
        }

        @Test
        @DisplayName("HP-RC-04: findById() retorna 200 OK con árbitro encontrado")
        void findByIdRetorna200() {
            RefereeResponse resp = buildResponse("find@gmail.com", "Árbitro Find");
            when(refereeService.findById("REF-FIND")).thenReturn(resp);

            ResponseEntity<RefereeResponse> response = controller.findById("REF-FIND");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("find@gmail.com", response.getBody().email());
        }

        @Test
        @DisplayName("HP-RC-05: findAll() retorna 200 OK con lista de árbitros")
        void findAllRetorna200() {
            when(refereeService.findAll()).thenReturn(List.of(
                    buildResponse("a@gmail.com", "Árbitro A"),
                    buildResponse("b@gmail.com", "Árbitro B")
            ));

            ResponseEntity<List<RefereeResponse>> response = controller.findAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-RC-01: create() propaga RefereeException si email duplicado")
        void createPropagaExcepcionEmailDuplicado() {
            CreateRefereeRequest req = new CreateRefereeRequest("Dup Árbitro", "dup@gmail.com");
            doThrow(new RefereeException("email",
                    RefereeException.EMAIL_ALREADY_REGISTERED.formatted("dup@gmail.com")))
                    .when(refereeService).create(req);

            assertThrows(RefereeException.class, () -> controller.create(req));
        }

        @Test
        @DisplayName("EP-RC-02: assignToMatch() propaga RefereeException si árbitro no existe")
        void assignPropagaExcepcionArbitroNoExiste() {
            AssignRefereeRequest req = new AssignRefereeRequest("NO-EXISTE");
            doThrow(new RefereeException("refereeId",
                    RefereeException.REFEREE_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(refereeService).assignToMatch(anyString(), eq(req));

            assertThrows(RefereeException.class,
                    () -> controller.assignToMatch("MATCH-001", req));
        }

        @Test
        @DisplayName("EP-RC-03: assignToMatch() propaga RefereeException si partido ya tiene árbitro")
        void assignPropagaExcepcionPartidoYaTieneArbitro() {
            AssignRefereeRequest req = new AssignRefereeRequest("REF-001");
            doThrow(new RefereeException("match", RefereeException.MATCH_ALREADY_HAS_REFEREE))
                    .when(refereeService).assignToMatch("MATCH-ASIGNADO", req);

            assertThrows(RefereeException.class,
                    () -> controller.assignToMatch("MATCH-ASIGNADO", req));
        }

        @Test
        @DisplayName("EP-RC-04: findById() propaga RefereeException si árbitro no existe")
        void findByIdPropagaExcepcion() {
            doThrow(new RefereeException("id",
                    RefereeException.REFEREE_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(refereeService).findById("NO-EXISTE");

            assertThrows(RefereeException.class, () -> controller.findById("NO-EXISTE"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-RC-01: findAll() retorna lista vacía con 200 OK si no hay árbitros")
        void findAllVacioConOk() {
            when(refereeService.findAll()).thenReturn(List.of());

            ResponseEntity<List<RefereeResponse>> response = controller.findAll();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("CS-RC-02: create() retorna el body exacto que devuelve el servicio")
        void createRetornaBodyDelServicio() {
            CreateRefereeRequest req = new CreateRefereeRequest("Body Test", "body@gmail.com");
            RefereeResponse esperado = buildResponse("body@gmail.com", "Body Test");
            when(refereeService.create(req)).thenReturn(esperado);

            ResponseEntity<RefereeResponse> response = controller.create(req);
            assertSame(esperado, response.getBody());
        }

        @Test
        @DisplayName("CS-RC-03: findById() llama al servicio exactamente una vez con el ID correcto")
        void findByIdLlamaServicioUnaVez() {
            when(refereeService.findById("REF-X")).thenReturn(buildResponse("x@gmail.com", "X"));
            controller.findById("REF-X");
            verify(refereeService, times(1)).findById("REF-X");
        }
    }

    // ── Helpers

    private RefereeResponse buildResponse(String email, String fullname) {
        return new RefereeResponse(UUID.randomUUID().toString(), fullname, email, List.of());
    }
}
