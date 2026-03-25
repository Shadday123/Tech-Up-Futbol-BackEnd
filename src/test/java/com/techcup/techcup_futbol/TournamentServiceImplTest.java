package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.DataStore;
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
            TournamentResponse resp = service.create(buildRequest("Torneo HP", 8));

            assertEquals(1, DataStore.torneos.size());
            assertEquals("DRAFT", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-02: create() genera un ID no nulo y no vacío")
        void createGeneraIdNoNulo() {
            TournamentResponse resp = service.create(buildRequest("Primer Torneo", 4));
            assertNotNull(resp.id());
            assertFalse(resp.id().isBlank());
        }

        @Test
        @DisplayName("HP-TOS-03: create() genera IDs únicos para torneos distintos")
        void createGeneraIdsUnicos() {
            String id1 = service.create(buildRequest("Torneo 1", 4)).id();
            String id2 = service.create(buildRequest("Torneo 2", 6)).id();
            String id3 = service.create(buildRequest("Torneo 3", 8)).id();

            assertNotEquals(id1, id2);
            assertNotEquals(id2, id3);
            assertNotEquals(id1, id3);
        }

        @Test
        @DisplayName("HP-TOS-04: create() persiste todos los campos del request")
        void createPersisteCampos() {
            String id = service.create(buildRequest("Torneo Campos", 10)).id();

            var guardado = DataStore.torneos.get(id);
            assertEquals("Torneo Campos", guardado.getName());
            assertEquals(10, guardado.getMaxTeams());
            assertEquals(150.0, guardado.getRegistrationFee());
        }

        @Test
        @DisplayName("HP-TOS-05: updateStatus() DRAFT → ACTIVE actualiza el estado")
        void updateStatusDraftAActive() {
            String id = service.create(buildRequest("Torneo Update", 4)).id();
            TournamentResponse resp = service.updateStatus(id, "ACTIVE");

            assertEquals("ACTIVE", resp.currentState());
            assertEquals(TournamentState.ACTIVE, DataStore.torneos.get(id).getCurrentState());
        }

        @Test
        @DisplayName("HP-TOS-06: updateStatus() ACTIVE → IN_PROGRESS")
        void updateStatusActiveAInProgress() {
            String id = service.create(buildRequest("Torneo Progress", 4)).id();
            service.updateStatus(id, "ACTIVE");
            TournamentResponse resp = service.updateStatus(id, "IN_PROGRESS");

            assertEquals("IN_PROGRESS", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-07: updateStatus() IN_PROGRESS → COMPLETED")
        void updateStatusInProgressACompleted() {
            String id = service.create(buildRequest("Torneo Complete", 4)).id();
            service.updateStatus(id, "ACTIVE");
            service.updateStatus(id, "IN_PROGRESS");
            TournamentResponse resp = service.updateStatus(id, "COMPLETED");

            assertEquals("COMPLETED", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-08: updateStatus() DRAFT → DELETED")
        void updateStatusDraftADeleted() {
            String id = service.create(buildRequest("Torneo Delete", 4)).id();
            TournamentResponse resp = service.updateStatus(id, "DELETED");

            assertEquals("DELETED", resp.currentState());
        }

        @Test
        @DisplayName("HP-TOS-09: findById() retorna TournamentResponse si existe")
        void findByIdRetornaRespuesta() {
            String id = service.create(buildRequest("Torneo Find", 6)).id();
            TournamentResponse resp = service.findById(id);

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
            assertTrue(service.findAll().isEmpty());
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
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(new CreateTournamentRequest("", START, END, 100.0, 4, "Reglas")));
            assertEquals("name", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-03: create() lanza TournamentException si fechas inválidas")
        void createFechasInvalidasLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(new CreateTournamentRequest("Torneo", END, START, 100.0, 4, "Reglas")));
            assertEquals("dates", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-04: create() lanza TournamentException si cuota negativa")
        void createCuotaNegativaLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(new CreateTournamentRequest("Torneo", START, END, -50.0, 4, "Reglas")));
            assertEquals("registrationFee", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-05: create() lanza TournamentException si maxTeams < 4")
        void createMaxTeamsBajoLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(new CreateTournamentRequest("Torneo", START, END, 100.0, 2, "Reglas")));
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
            String id = service.create(buildRequest("Torneo Invalido", 4)).id();
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.updateStatus(id, "ESTADO_INEXISTENTE"));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-08: updateStatus() lanza TournamentException en transición inválida DRAFT → IN_PROGRESS")
        void updateStatusTransicionInvalidaLanzaExcepcion() {
            String id = service.create(buildRequest("Torneo Trans", 4)).id();
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.updateStatus(id, "IN_PROGRESS"));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-09: updateStatus() lanza TournamentException en COMPLETED → cualquier estado")
        void updateStatusCompletedEsTerminal() {
            String id = service.create(buildRequest("Torneo Comp", 4)).id();
            service.updateStatus(id, "ACTIVE");
            service.updateStatus(id, "IN_PROGRESS");
            service.updateStatus(id, "COMPLETED");

            assertThrows(TournamentException.class,
                    () -> service.updateStatus(id, "DRAFT"));
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
            String id = service.create(buildRequest("Flujo Completo", 8)).id();
            assertDoesNotThrow(() -> {
                service.updateStatus(id, "ACTIVE");
                service.updateStatus(id, "IN_PROGRESS");
                service.updateStatus(id, "COMPLETED");
            });
            assertEquals(TournamentState.COMPLETED, DataStore.torneos.get(id).getCurrentState());
        }

        @Test
        @DisplayName("CS-TOS-02: create() con cuota 0.0 es válido (límite inferior)")
        void createCuotaCeroEsValido() {
            assertDoesNotThrow(() ->
                    service.create(new CreateTournamentRequest("Torneo Gratis", START, END, 0.0, 4, "Reglas")));
        }

        @Test
        @DisplayName("CS-TOS-03: create() con maxTeams impar es inválido")
        void createMaxTeamsImparInvalido() {
            assertThrows(TournamentException.class, () ->
                    service.create(new CreateTournamentRequest("Torneo Impar", START, END, 100.0, 5, "Reglas")));
        }

        @Test
        @DisplayName("CS-TOS-04: updateStatus() acepta nombre de estado en minúsculas")
        void updateStatusAceptaMinusculas() {
            String id = service.create(buildRequest("Torneo Minus", 4)).id();
            assertDoesNotThrow(() -> service.updateStatus(id, "active"));
            assertEquals(TournamentState.ACTIVE, DataStore.torneos.get(id).getCurrentState());
        }

        @Test
        @DisplayName("CS-TOS-05: findAll() refleja estado actualizado después de updateStatus")
        void findAllReflejaEstadoActualizado() {
            String id = service.create(buildRequest("Torneo Reflect", 4)).id();
            service.updateStatus(id, "ACTIVE");

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
            assertEquals(5, DataStore.torneos.keySet().stream().distinct().count());
        }

        @Test
        @DisplayName("CS-TOS-08: DELETED es terminal — no permite ninguna transición posterior")
        void deletedEsTerminalNoPermiteTransicion() {
            String id = service.create(buildRequest("Torneo Deleted", 4)).id();
            service.updateStatus(id, "DELETED");

            for (TournamentState next : TournamentState.values()) {
                assertThrows(TournamentException.class,
                        () -> service.updateStatus(id, next.name()),
                        "Debería fallar la transición DELETED → " + next);
            }
        }
    }

    // ── Helpers

    private CreateTournamentRequest buildRequest(String name, int maxTeams) {
        return new CreateTournamentRequest(name, START, END, 150.0, maxTeams, "Reglas estándar");
    }
}
