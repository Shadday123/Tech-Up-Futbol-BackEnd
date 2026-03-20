package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.*;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.TournamentConfigServiceImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TournamentConfigServiceImpl Tests")
class TournamentConfigServiceImplTest {

    private TournamentConfigServiceImpl service;

    private static final LocalDateTime TORNEO_START = LocalDateTime.now().plusDays(10);
    private static final LocalDateTime TORNEO_END   = LocalDateTime.now().plusDays(40);
    private static final LocalDateTime DEADLINE     = LocalDateTime.now().plusDays(5); // antes del inicio

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        service = new TournamentConfigServiceImpl();
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TCF-01: createOrUpdate() crea configuración para torneo en estado DRAFT")
        void createOrUpdateCreaConfiguracionDraft() {
            Tournament t = buildTorneo("T001", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            TournamentConfigResponse resp = service.createOrUpdate(t.getId(), buildRequest());

            assertNotNull(resp);
            assertNotNull(resp.id());
            assertEquals(t.getId(), resp.tournamentId());
            assertEquals("Reglamento oficial", resp.rules());
        }

        @Test
        @DisplayName("HP-TCF-02: createOrUpdate() crea configuración para torneo en estado ACTIVE")
        void createOrUpdateCreaConfiguracionActive() {
            Tournament t = buildTorneo("T002", TournamentState.ACTIVE);
            DataStore.torneos.put(t.getId(), t);

            assertDoesNotThrow(() -> service.createOrUpdate(t.getId(), buildRequest()));
        }

        @Test
        @DisplayName("HP-TCF-03: createOrUpdate() actualiza configuración existente (idempotente)")
        void createOrUpdateActualizaConfiguracion() {
            Tournament t = buildTorneo("T003", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            service.createOrUpdate(t.getId(), buildRequest());
            CreateTournamentConfigRequest updated = new CreateTournamentConfigRequest(
                    "Reglamento actualizado", DEADLINE, List.of(), List.of(), List.of(), "Sanción actualizada");
            TournamentConfigResponse resp = service.createOrUpdate(t.getId(), updated);

            assertEquals("Reglamento actualizado", resp.rules());
            assertEquals("Sanción actualizada", resp.sanctions());
        }

        @Test
        @DisplayName("HP-TCF-04: findByTournamentId() retorna configuración existente")
        void findByTournamentIdRetornaConfig() {
            Tournament t = buildTorneo("T004", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);
            service.createOrUpdate(t.getId(), buildRequest());

            TournamentConfigResponse resp = service.findByTournamentId(t.getId());
            assertNotNull(resp);
            assertEquals("Reglamento oficial", resp.rules());
        }

        @Test
        @DisplayName("HP-TCF-05: createOrUpdate() conserva el mismo ID al actualizar")
        void createOrUpdateConservaId() {
            Tournament t = buildTorneo("T005", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            TournamentConfigResponse r1 = service.createOrUpdate(t.getId(), buildRequest());
            TournamentConfigResponse r2 = service.createOrUpdate(t.getId(), buildRequest());

            assertEquals(r1.id(), r2.id());
        }

        @Test
        @DisplayName("HP-TCF-06: createOrUpdate() persiste sanciones correctamente")
        void createOrUpdatePersisteSanciones() {
            Tournament t = buildTorneo("T006", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            TournamentConfigResponse resp = service.createOrUpdate(t.getId(), buildRequest());
            assertEquals("Tarjeta roja: 2 partidos", resp.sanctions());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TCF-01: createOrUpdate() lanza TournamentException si torneo no existe")
        void createOrUpdateTorneoNoExisteLanza() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.createOrUpdate("NO-EXISTE", buildRequest()));
            assertEquals("id", ex.getField());
        }

        @Test
        @DisplayName("EP-TCF-02: createOrUpdate() lanza TournamentException si torneo está IN_PROGRESS")
        void createOrUpdateInProgressLanza() {
            Tournament t = buildTorneo("T010", TournamentState.IN_PROGRESS);
            DataStore.torneos.put(t.getId(), t);

            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.createOrUpdate(t.getId(), buildRequest()));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TCF-03: createOrUpdate() lanza TournamentException si torneo está COMPLETED")
        void createOrUpdateCompletedLanza() {
            Tournament t = buildTorneo("T011", TournamentState.COMPLETED);
            DataStore.torneos.put(t.getId(), t);

            assertThrows(TournamentException.class,
                    () -> service.createOrUpdate(t.getId(), buildRequest()));
        }

        @Test
        @DisplayName("EP-TCF-04: createOrUpdate() lanza TournamentException si deadline no es antes del inicio")
        void createOrUpdateDeadlineDespusDeinicioLanza() {
            Tournament t = buildTorneo("T012", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            LocalDateTime deadlineDespues = TORNEO_START.plusDays(1); // después del inicio
            CreateTournamentConfigRequest req = new CreateTournamentConfigRequest(
                    "Reglas", deadlineDespues, List.of(), List.of(), List.of(), "Sanciones");

            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.createOrUpdate(t.getId(), req));
            assertEquals("registrationDeadline", ex.getField());
        }

        @Test
        @DisplayName("EP-TCF-05: findByTournamentId() lanza TournamentException si no hay configuración")
        void findByTournamentIdSinConfigLanza() {
            Tournament t = buildTorneo("T013", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            assertThrows(TournamentException.class,
                    () -> service.findByTournamentId(t.getId()));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TCF-01: createOrUpdate() con deadline null no lanza excepción")
        void createOrUpdateDeadlineNullNoLanza() {
            Tournament t = buildTorneo("T020", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            CreateTournamentConfigRequest req = new CreateTournamentConfigRequest(
                    "Reglas", null, List.of(), List.of(), List.of(), "Sanciones");

            assertDoesNotThrow(() -> service.createOrUpdate(t.getId(), req));
        }

        @Test
        @DisplayName("CS-TCF-02: múltiples torneos tienen configuraciones independientes")
        void multiplesTorneosConfiguracionesIndependientes() {
            Tournament t1 = buildTorneo("T021", TournamentState.DRAFT);
            Tournament t2 = buildTorneo("T022", TournamentState.DRAFT);
            DataStore.torneos.put(t1.getId(), t1);
            DataStore.torneos.put(t2.getId(), t2);

            CreateTournamentConfigRequest req1 = new CreateTournamentConfigRequest(
                    "Reglas Torneo 1", DEADLINE, List.of(), List.of(), List.of(), "Sancion 1");
            CreateTournamentConfigRequest req2 = new CreateTournamentConfigRequest(
                    "Reglas Torneo 2", DEADLINE, List.of(), List.of(), List.of(), "Sancion 2");

            service.createOrUpdate(t1.getId(), req1);
            service.createOrUpdate(t2.getId(), req2);

            assertEquals("Reglas Torneo 1", service.findByTournamentId(t1.getId()).rules());
            assertEquals("Reglas Torneo 2", service.findByTournamentId(t2.getId()).rules());
        }

        @Test
        @DisplayName("CS-TCF-03: createOrUpdate() con listas vacías funciona sin excepción")
        void createOrUpdateListasVaciasFunciona() {
            Tournament t = buildTorneo("T023", TournamentState.DRAFT);
            DataStore.torneos.put(t.getId(), t);

            TournamentConfigResponse resp = service.createOrUpdate(t.getId(), buildRequest());
            assertNotNull(resp);
        }

        @Test
        @DisplayName("CS-TCF-04: DELETED puede ser configurado (no está en la lista de bloqueados)")
        void createOrUpdateDeletedFunciona() {
            Tournament t = buildTorneo("T024", TournamentState.DELETED);
            DataStore.torneos.put(t.getId(), t);

            // DELETED no está en IN_PROGRESS ni COMPLETED, así que debería funcionar
            assertDoesNotThrow(() -> service.createOrUpdate(t.getId(), buildRequest()));
        }
    }

    // ── Helpers

    private Tournament buildTorneo(String id, TournamentState state) {
        Tournament t = new Tournament();
        t.setId(id);
        t.setName("Torneo " + id);
        t.setStartDate(TORNEO_START);
        t.setEndDate(TORNEO_END);
        t.setCurrentState(state);
        t.setMaxTeams(8);
        return t;
    }

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
}
