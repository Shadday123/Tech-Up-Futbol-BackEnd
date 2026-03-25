package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.TournamentConfigController;
import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.*;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.service.TournamentConfigService;
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
@DisplayName("TournamentConfigController Tests")
class TournamentConfigControllerTest {

    @Mock
    private TournamentConfigService configService;

    private TournamentConfigController controller;

    private static final LocalDateTime DEADLINE = LocalDateTime.now().plusDays(5);

    @BeforeEach
    void setUp() {
        controller = new TournamentConfigController(configService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TCC-01: createOrUpdate() retorna 200 OK con la configuración guardada")
        void createOrUpdateRetorna200() {
            CreateTournamentConfigRequest req = buildRequest();
            TournamentConfigResponse resp = buildResponse("T001");
            when(configService.createOrUpdate("T001", req)).thenReturn(resp);

            ResponseEntity<TournamentConfigResponse> response =
                    controller.createOrUpdate("T001", req);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("T001", response.getBody().tournamentId());
        }

        @Test
        @DisplayName("HP-TCC-02: createOrUpdate() llama al servicio con los parámetros correctos")
        void createOrUpdateLlamaServicio() {
            CreateTournamentConfigRequest req = buildRequest();
            when(configService.createOrUpdate("T002", req)).thenReturn(buildResponse("T002"));

            controller.createOrUpdate("T002", req);
            verify(configService, times(1)).createOrUpdate("T002", req);
        }

        @Test
        @DisplayName("HP-TCC-03: findByTournament() retorna 200 OK con la configuración")
        void findByTournamentRetorna200() {
            TournamentConfigResponse resp = buildResponse("T001");
            when(configService.findByTournamentId("T001")).thenReturn(resp);

            ResponseEntity<TournamentConfigResponse> response =
                    controller.findByTournament("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Reglamento oficial", response.getBody().rules());
        }

        @Test
        @DisplayName("HP-TCC-04: findByTournament() llama al servicio exactamente una vez")
        void findByTournamentLlamaServicio() {
            when(configService.findByTournamentId("T-CHECK"))
                    .thenReturn(buildResponse("T-CHECK"));

            controller.findByTournament("T-CHECK");
            verify(configService, times(1)).findByTournamentId("T-CHECK");
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TCC-01: createOrUpdate() propaga TournamentException si torneo no existe")
        void createOrUpdatePropagaExcepcionTorneoNoExiste() {
            CreateTournamentConfigRequest req = buildRequest();
            doThrow(new TournamentException("id",
                    TournamentException.TOURNAMENT_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(configService).createOrUpdate("NO-EXISTE", req);

            assertThrows(TournamentException.class,
                    () -> controller.createOrUpdate("NO-EXISTE", req));
        }

        @Test
        @DisplayName("EP-TCC-02: createOrUpdate() propaga TournamentException si torneo IN_PROGRESS")
        void createOrUpdatePropagaExcepcionEstadoInvalido() {
            CreateTournamentConfigRequest req = buildRequest();
            doThrow(new TournamentException("state",
                    "Solo se pueden configurar torneos en estado Borrador o Activo."))
                    .when(configService).createOrUpdate("T001", req);

            TournamentException ex = assertThrows(TournamentException.class,
                    () -> controller.createOrUpdate("T001", req));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TCC-03: findByTournament() propaga TournamentException si no hay configuración")
        void findByTournamentPropagaExcepcionSinConfig() {
            doThrow(new TournamentException("config", "No existe configuración para el torneo."))
                    .when(configService).findByTournamentId("T-SIN-CONFIG");

            assertThrows(TournamentException.class,
                    () -> controller.findByTournament("T-SIN-CONFIG"));
        }

        @Test
        @DisplayName("EP-TCC-04: createOrUpdate() propaga TournamentException si deadline después del inicio")
        void createOrUpdateDeadlineDespuesDelInicioLanza() {
            CreateTournamentConfigRequest req = buildRequest();
            doThrow(new TournamentException("registrationDeadline",
                    "La fecha de cierre debe ser anterior al inicio."))
                    .when(configService).createOrUpdate("T001", req);

            assertThrows(TournamentException.class,
                    () -> controller.createOrUpdate("T001", req));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TCC-01: createOrUpdate() retorna el body exacto del servicio")
        void createOrUpdateRetornaBodyDelServicio() {
            CreateTournamentConfigRequest req = buildRequest();
            TournamentConfigResponse esperado = buildResponse("T001");
            when(configService.createOrUpdate("T001", req)).thenReturn(esperado);

            ResponseEntity<TournamentConfigResponse> response =
                    controller.createOrUpdate("T001", req);

            assertSame(esperado, response.getBody());
        }

        @Test
        @DisplayName("CS-TCC-02: createOrUpdate() se puede llamar múltiples veces con el mismo torneo")
        void createOrUpdateIdempotente() {
            CreateTournamentConfigRequest req = buildRequest();
            when(configService.createOrUpdate(eq("T001"), any()))
                    .thenReturn(buildResponse("T001"));

            controller.createOrUpdate("T001", req);
            controller.createOrUpdate("T001", req);

            verify(configService, times(2)).createOrUpdate(eq("T001"), eq(req));
        }

        @Test
        @DisplayName("CS-TCC-03: findByTournament() retorna el body exacto del servicio")
        void findByTournamentRetornaBodyDelServicio() {
            TournamentConfigResponse esperado = buildResponse("T001");
            when(configService.findByTournamentId("T001")).thenReturn(esperado);

            ResponseEntity<TournamentConfigResponse> response =
                    controller.findByTournament("T001");

            assertSame(esperado, response.getBody());
        }
    }

    // ── Helpers

    private CreateTournamentConfigRequest buildRequest() {
        return new CreateTournamentConfigRequest(
                "Reglamento oficial",
                DEADLINE,
                List.of(new ImportantDateDTO("Inscripciones", DEADLINE)),
                List.of(new MatchScheduleDTO("Sábado", "09:00", "11:00")),
                List.of(new FieldDTO("Cancha 1", "Bloque A")),
                "Tarjeta roja: 2 partidos"
        );
    }

    private TournamentConfigResponse buildResponse(String tournamentId) {
        return new TournamentConfigResponse(
                UUID.randomUUID().toString(),
                tournamentId,
                "Reglamento oficial",
                DEADLINE,
                List.of(),
                List.of(),
                List.of(),
                "Tarjeta roja: 2 partidos"
        );
    }
}
