package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentServiceImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TournamentServiceImpl Tests")
class TournamentServiceImplTest {

    private TournamentServiceImpl service;

    private static final LocalDateTime START = LocalDateTime.now().plusDays(5);
    private static final LocalDateTime END   = LocalDateTime.now().plusDays(30);

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        service = new TournamentServiceImpl();
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TOS-01: create() guarda el torneo en DataStore con estado DRAFT")
        void createGuardaTorneoEnDraft() {
            CreateTournamentRequest req = buildRequest("Torneo HP", 8);
            TournamentResponse resp = service.create(req);

            assertEquals(1, DataStore.torneos.size());
            assertEquals("DRAFT", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-02: create() genera ID T001 cuando DataStore está vacío")
        void createGeneraIdT001() {
            TournamentResponse resp = service.create(buildRequest("Primer Torneo", 4));
            assertEquals("T001", resp.id());
        }

        @Test
        @DisplayName("HP-TOS-03: create() genera IDs secuenciales T001, T002, T003")
        void createGeneraIdsSecuenciales() {
            TournamentResponse r1 = service.create(buildRequest("Torneo 1", 4));
            TournamentResponse r2 = service.create(buildRequest("Torneo 2", 6));
            TournamentResponse r3 = service.create(buildRequest("Torneo 3", 8));

            assertEquals("T001", r1.id());
            assertEquals("T002", r2.id());
            assertEquals("T003", r3.id());
        }

        @Test
        @DisplayName("HP-TOS-04: create() persiste todos los campos del request")
        void createPersisteCampos() {
            CreateTournamentRequest req = buildRequest("Torneo Campos", 10);
            service.create(req);

            Tournament guardado = DataStore.torneos.get("T001");
            assertEquals("Torneo Campos", guardado.getName());
            assertEquals(10, guardado.getMaxTeams());
            assertEquals(150.0, guardado.getRegistrationFee());
        }

        @Test
        @DisplayName("HP-TOS-05: updateStatus() DRAFT → ACTIVE actualiza el estado")
        void updateStatusDraftAActive() {
            service.create(buildRequest("Torneo Update", 4));
            TournamentResponse resp = service.updateStatus("T001", "ACTIVE");

            assertEquals("ACTIVE", resp.currentState());
            assertEquals(TournamentState.ACTIVE, DataStore.torneos.get("T001").getCurrentState());
        }

        @Test
        @DisplayName("HP-TOS-06: updateStatus() ACTIVE → IN_PROGRESS")
        void updateStatusActiveAInProgress() {
            service.create(buildRequest("Torneo Progress", 4));
            service.updateStatus("T001", "ACTIVE");
            TournamentResponse resp = service.updateStatus("T001", "IN_PROGRESS");

            assertEquals("IN_PROGRESS", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-07: updateStatus() IN_PROGRESS → COMPLETED")
        void updateStatusInProgressACompleted() {
            service.create(buildRequest("Torneo Complete", 4));
            service.updateStatus("T001", "ACTIVE");
            service.updateStatus("T001", "IN_PROGRESS");
            TournamentResponse resp = service.updateStatus("T001", "COMPLETED");

            assertEquals("COMPLETED", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-08: updateStatus() DRAFT → DELETED")
        void updateStatusDraftADeleted() {
            service.create(buildRequest("Torneo Delete", 4));
            TournamentResponse resp = service.updateStatus("T001", "DELETED");

            assertEquals("DELETED", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-09: findById() retorna TournamentResponse si existe")
        void findByIdRetornaRespuesta() {
            service.create(buildRequest("Torneo Find", 6));
            TournamentResponse resp = service.findById("T001");

            assertNotNull(resp);
            assertEquals("Torneo Find", resp.name());
        }

        @Test
        @DisplayName("HP-TOS-10: findAll() retorna todos los torneos")
        void findAllRetornaTodos() {
            service.create(buildRequest("T-A", 4));
            service.create(buildRequest("T-B", 6));
            service.create(buildRequest("T-C", 8));

            List<TournamentResponse> lista = service.findAll();
            assertEquals(3, lista.size());
        }

        @Test
        @DisplayName("HP-TOS-11: findAll() retorna lista vacía si no hay torneos")
        void findAllRetornaVacio() {
            List<TournamentResponse> lista = service.findAll();
            assertTrue(lista.isEmpty());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TOS-01: create() lanza TournamentException si request es null")
        void createRequestNullLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(null));
            assertEquals(TournamentException.REQUEST_NULL, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TOS-02: create() lanza TournamentException si nombre vacío")
        void createNombreVacioLanzaExcepcion() {
            CreateTournamentRequest req = new CreateTournamentRequest("", START, END, 100.0, 4, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(req));
            assertEquals("name", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-03: create() lanza TournamentException si fechas inválidas")
        void createFechasInvalidasLanzaExcepcion() {
            CreateTournamentRequest req = new CreateTournamentRequest("Torneo", END, START, 100.0, 4, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(req));
            assertEquals("dates", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-04: create() lanza TournamentException si cuota negativa")
        void createCuotaNegativaLanzaExcepcion() {
            CreateTournamentRequest req = new CreateTournamentRequest("Torneo", START, END, -50.0, 4, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(req));
            assertEquals("registrationFee", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-05: create() lanza TournamentException si maxTeams < 4")
        void createMaxTeamsBajoLanzaExcepcion() {
            CreateTournamentRequest req = new CreateTournamentRequest("Torneo", START, END, 100.0, 2, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(req));
            assertEquals("maxTeams", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-06: updateStatus() lanza TournamentException si torneo no existe")
        void updateStatusTorneoNoExisteLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.updateStatus("NO-EXISTE", "ACTIVE"));
            assertEquals("id", ex.getField());
            assertTrue(ex.getMessage().contains("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-TOS-07: updateStatus() lanza TournamentException si nombre de estado inválido")
        void updateStatusEstadoInvalidoLanzaExcepcion() {
            service.create(buildRequest("Torneo Invalido", 4));
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.updateStatus("T001", "ESTADO_INEXISTENTE"));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-08: updateStatus() lanza TournamentException en transición inválida DRAFT → IN_PROGRESS")
        void updateStatusTransicionInvalidaLanzaExcepcion() {
            service.create(buildRequest("Torneo Trans", 4));
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.updateStatus("T001", "IN_PROGRESS"));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-09: updateStatus() lanza TournamentException en COMPLETED → cualquier estado")
        void updateStatusCompletedEsTerminal() {
            service.create(buildRequest("Torneo Comp", 4));
            service.updateStatus("T001", "ACTIVE");
            service.updateStatus("T001", "IN_PROGRESS");
            service.updateStatus("T001", "COMPLETED");

            assertThrows(TournamentException.class,
                    () -> service.updateStatus("T001", "DRAFT"));
        }

        @Test
        @DisplayName("EP-TOS-10: findById() lanza TournamentException si torneo no existe")
        void findByIdNoExisteLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.findById("T999"));
            assertEquals("id", ex.getField());
            assertTrue(ex.getMessage().contains("T999"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TOS-01: flujo completo DRAFT→ACTIVE→IN_PROGRESS→COMPLETED sin errores")
        void flujoCompletoSinErrores() {
            service.create(buildRequest("Flujo Completo", 8));
            assertDoesNotThrow(() -> {
                service.updateStatus("T001", "ACTIVE");
                service.updateStatus("T001", "IN_PROGRESS");
                service.updateStatus("T001", "COMPLETED");
            });
            assertEquals(TournamentState.COMPLETED, DataStore.torneos.get("T001").getCurrentState());
        }

        @Test
        @DisplayName("CS-TOS-02: create() con cuota 0.0 es válido (límite inferior)")
        void createCuotaCeroEsValido() {
            CreateTournamentRequest req = new CreateTournamentRequest("Torneo Gratis", START, END, 0.0, 4, "Reglas");
            assertDoesNotThrow(() -> service.create(req));
        }

        @Test
        @DisplayName("CS-TOS-03: create() con maxTeams impar es inválido (lanza excepción)")
        void createMaxTeamsImparInvalido() {
            CreateTournamentRequest req = new CreateTournamentRequest("Torneo Impar", START, END, 100.0, 5, "Reglas");
            assertThrows(TournamentException.class, () -> service.create(req));
        }

        @Test
        @DisplayName("CS-TOS-04: updateStatus() acepta nombre de estado en minúsculas")
        void updateStatusAceptaMinusculas() {
            service.create(buildRequest("Torneo Minus", 4));
            assertDoesNotThrow(() -> service.updateStatus("T001", "active"));
            assertEquals(TournamentState.ACTIVE, DataStore.torneos.get("T001").getCurrentState());
        }

        @Test
        @DisplayName("CS-TOS-05: findAll() refleja estado actualizado después de updateStatus")
        void findAllReflejaEstadoActualizado() {
            service.create(buildRequest("Torneo Reflect", 4));
            service.updateStatus("T001", "ACTIVE");

            List<TournamentResponse> lista = service.findAll();
            assertEquals(1, lista.size());
            assertEquals("ACTIVE", lista.get(0).currentState());
        }

        @Test
        @DisplayName("CS-TOS-06: create() con maxTeams 4 (mínimo válido par) no lanza excepción")
        void createMaxTeamsCuatroEsValido() {
            assertDoesNotThrow(() ->
                    service.create(new CreateTournamentRequest("Min Teams", START, END, 100.0, 4, "Reglas")));
        }

        @Test
        @DisplayName("CS-TOS-07: create() genera IDs no repetidos con múltiples torneos")
        void createIdsUnicoMultiples() {
            for (int i = 1; i <= 5; i++) {
                service.create(buildRequest("Torneo " + i, 4));
            }
            assertEquals(5, DataStore.torneos.size());
            // Todos con IDs distintos
            assertEquals(5, DataStore.torneos.keySet().stream().distinct().count());
        }

        @Test
        @DisplayName("CS-TOS-08: DELETED es terminal — no permite ninguna transición posterior")
        void deletedEsTerminalNoPermiteTransicion() {
            service.create(buildRequest("Torneo Deleted", 4));
            service.updateStatus("T001", "DELETED");

            for (TournamentState next : TournamentState.values()) {
                assertThrows(TournamentException.class,
                        () -> service.updateStatus("T001", next.name()),
                        "Debería fallar la transición DELETED → " + next);
            }
        }
    }

    // ── Helpers

    private CreateTournamentRequest buildRequest(String name, int maxTeams) {
        return new CreateTournamentRequest(name, START, END, 150.0, maxTeams, "Reglas estándar");
    }
}
