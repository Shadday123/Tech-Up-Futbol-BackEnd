package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentServiceImpl;
import com.techcup.techcup_futbol.core.exception.ResourceNotFoundException;
import com.techcup.techcup_futbol.repository.TournamentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TournamentServiceImpl Tests")
class TournamentServiceImplTest {

    @InjectMocks
    private TournamentServiceImpl service;

    @Mock
    private TournamentRepository tournamentRepository;

    private static final LocalDateTime START = LocalDateTime.now().plusDays(5);
    private static final LocalDateTime END   = LocalDateTime.now().plusDays(30);

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> {
            Tournament t = inv.getArgument(0);
            DataStore.torneos.put(t.getId(), t);
            return t;
        });
        when(tournamentRepository.findById(anyString()))
                .thenAnswer(inv -> java.util.Optional.ofNullable(DataStore.torneos.get(inv.getArgument(0))));
        when(tournamentRepository.findAll())
                .thenAnswer(inv -> new ArrayList<>(DataStore.torneos.values()));
    }

    // ── Helpers

    private Tournament buildTournament(String name, int maxTeams) {
        Tournament t = new Tournament();
        t.setName(name);
        t.setStartDate(START);
        t.setEndDate(END);
        t.setRegistrationFee(150.0);
        t.setMaxTeams(maxTeams);
        t.setRules("Reglas estándar");
        return t;
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TOS-01: create() guarda el torneo en DataStore con estado DRAFT")
        void createGuardaTorneoEnDraft() {
            Tournament resp = service.create(buildTournament("Torneo HP", 8));

            assertEquals(1, DataStore.torneos.size());
            assertEquals(TournamentState.DRAFT, resp.getCurrentState());
        }

        @Test
        @DisplayName("HP-TOS-02: create() genera un ID no nulo y no vacío")
        void createGeneraIdNoNulo() {
            Tournament resp = service.create(buildTournament("Primer Torneo", 4));
            assertNotNull(resp.getId());
            assertFalse(resp.getId().isBlank());
        }

        @Test
        @DisplayName("HP-TOS-03: create() genera IDs únicos para torneos distintos")
        void createGeneraIdsUnicos() {
            String id1 = service.create(buildTournament("Torneo 1", 4)).getId();
            String id2 = service.create(buildTournament("Torneo 2", 6)).getId();
            String id3 = service.create(buildTournament("Torneo 3", 8)).getId();

            assertNotEquals(id1, id2);
            assertNotEquals(id2, id3);
            assertNotEquals(id1, id3);
        }

        @Test
        @DisplayName("HP-TOS-04: create() persiste todos los campos del request")
        void createPersisteCampos() {
            Tournament resp = service.create(buildTournament("Torneo Campos", 10));

            assertEquals("Torneo Campos", resp.getName());
            assertEquals(10, resp.getMaxTeams());
            assertEquals(150.0, resp.getRegistrationFee());
        }

        @Test
        @DisplayName("HP-TOS-05: updateStatus() DRAFT → ACTIVE actualiza el estado")
        void updateStatusDraftAActive() {
            String id = service.create(buildTournament("Torneo Update", 4)).getId();
            Tournament resp = service.updateStatus(id, "ACTIVE");

            assertEquals(TournamentState.ACTIVE, resp.getCurrentState());
            assertEquals(TournamentState.ACTIVE, DataStore.torneos.get(id).getCurrentState());
        }

        @Test
        @DisplayName("HP-TOS-06: updateStatus() ACTIVE → IN_PROGRESS")
        void updateStatusActiveAInProgress() {
            String id = service.create(buildTournament("Torneo Progress", 4)).getId();
            service.updateStatus(id, "ACTIVE");
            Tournament resp = service.updateStatus(id, "IN_PROGRESS");

            assertEquals(TournamentState.IN_PROGRESS, resp.getCurrentState());
        }

        @Test
        @DisplayName("HP-TOS-07: updateStatus() IN_PROGRESS → COMPLETED")
        void updateStatusInProgressACompleted() {
            String id = service.create(buildTournament("Torneo Complete", 4)).getId();
            service.updateStatus(id, "ACTIVE");
            service.updateStatus(id, "IN_PROGRESS");
            Tournament resp = service.updateStatus(id, "COMPLETED");

            assertEquals(TournamentState.COMPLETED, resp.getCurrentState());
        }

        @Test
        @DisplayName("HP-TOS-08: updateStatus() DRAFT → DELETED")
        void updateStatusDraftADeleted() {
            String id = service.create(buildTournament("Torneo Delete", 4)).getId();
            Tournament resp = service.updateStatus(id, "DELETED");

            assertEquals(TournamentState.DELETED, resp.getCurrentState());
        }

        @Test
        @DisplayName("HP-TOS-09: findById() retorna Tournament si existe")
        void findByIdRetornaRespuesta() {
            String id = service.create(buildTournament("Torneo Find", 6)).getId();
            Tournament resp = service.findById(id);

            assertNotNull(resp);
            assertEquals("Torneo Find", resp.getName());
        }

        @Test
        @DisplayName("HP-TOS-10: findAll() retorna todos los torneos")
        void findAllRetornaTodos() {
            service.create(buildTournament("T-A", 4));
            service.create(buildTournament("T-B", 6));
            service.create(buildTournament("T-C", 8));

            List<Tournament> lista = service.findAll();
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
        @DisplayName("EP-TOS-01: findById() con ID inexistente lanza ResourceNotFoundException")
        void findByIdInexistenteLanzaResourceNotFoundException() {
            assertThrows(ResourceNotFoundException.class,
                    () -> service.findById("id-que-no-existe"));
        }

        @Test
        @DisplayName("EP-TOS-02: create() lanza TournamentException si nombre vacío")
        void createNombreVacioLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(buildTournament("", 4)));
            assertEquals("name", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-03: create() lanza TournamentException si fechas inválidas")
        void createFechasInvalidasLanzaExcepcion() {
            Tournament t = buildTournament("Torneo", 4);
            t.setStartDate(END);
            t.setEndDate(START);
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(t));
            assertEquals("dates", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-04: create() lanza TournamentException si cuota negativa")
        void createCuotaNegativaLanzaExcepcion() {
            Tournament t = buildTournament("Torneo", 4);
            t.setRegistrationFee(-50.0);
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(t));
            assertEquals("registrationFee", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-05: create() lanza TournamentException si maxTeams < 2")
        void createMaxTeamsBajoLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(buildTournament("Torneo", 1)));
            assertEquals("maxTeams", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-06: create() lanza TournamentException si maxTeams es impar")
        void createMaxTeamsImparLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.create(buildTournament("Torneo", 5)));
            assertEquals("maxTeams", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-07: updateStatus() lanza TournamentException si nombre de estado inválido")
        void updateStatusEstadoInvalidoLanzaExcepcion() {
            String id = service.create(buildTournament("Torneo Invalido", 4)).getId();
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.updateStatus(id, "ESTADO_INEXISTENTE"));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-08: updateStatus() lanza TournamentException en transición inválida DRAFT → IN_PROGRESS")
        void updateStatusTransicionInvalidaLanzaExcepcion() {
            String id = service.create(buildTournament("Torneo Trans", 4)).getId();
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.updateStatus(id, "IN_PROGRESS"));
            assertEquals("state", ex.getField());
        }

        @Test
        @DisplayName("EP-TOS-09: updateStatus() lanza TournamentException en COMPLETED → cualquier estado")
        void updateStatusCompletedEsTerminal() {
            String id = service.create(buildTournament("Torneo Comp", 4)).getId();
            service.updateStatus(id, "ACTIVE");
            service.updateStatus(id, "IN_PROGRESS");
            service.updateStatus(id, "COMPLETED");

            assertThrows(TournamentException.class,
                    () -> service.updateStatus(id, "DRAFT"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TOS-01: flujo completo DRAFT→ACTIVE→IN_PROGRESS→COMPLETED sin errores")
        void flujoCompletoSinErrores() {
            String id = service.create(buildTournament("Flujo Completo", 8)).getId();
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
            Tournament t = buildTournament("Torneo Gratis", 4);
            t.setRegistrationFee(0.0);
            assertDoesNotThrow(() -> service.create(t));
        }

        @Test
        @DisplayName("CS-TOS-03: create() con maxTeams impar es inválido")
        void createMaxTeamsImparInvalido() {
            assertThrows(TournamentException.class, () ->
                    service.create(buildTournament("Torneo Impar", 5)));
        }

        @Test
        @DisplayName("CS-TOS-04: updateStatus() acepta nombre de estado en minúsculas")
        void updateStatusAceptaMinusculas() {
            String id = service.create(buildTournament("Torneo Minus", 4)).getId();
            assertDoesNotThrow(() -> service.updateStatus(id, "active"));
            assertEquals(TournamentState.ACTIVE, DataStore.torneos.get(id).getCurrentState());
        }

        @Test
        @DisplayName("CS-TOS-05: findAll() refleja estado actualizado después de updateStatus")
        void findAllReflejaEstadoActualizado() {
            String id = service.create(buildTournament("Torneo Reflect", 4)).getId();
            service.updateStatus(id, "ACTIVE");

            List<Tournament> lista = service.findAll();
            assertEquals(1, lista.size());
            assertEquals(TournamentState.ACTIVE, lista.get(0).getCurrentState());
        }

        @Test
        @DisplayName("CS-TOS-06: create() con maxTeams 4 (mínimo válido par) no lanza excepción")
        void createMaxTeamsCuatroEsValido() {
            assertDoesNotThrow(() -> service.create(buildTournament("Min Teams", 4)));
        }

        @Test
        @DisplayName("CS-TOS-07: create() genera IDs no repetidos con múltiples torneos")
        void createIdsUnicoMultiples() {
            for (int i = 1; i <= 5; i++) {
                service.create(buildTournament("Torneo " + i, 4));
            }
            assertEquals(5, DataStore.torneos.size());
            assertEquals(5, DataStore.torneos.keySet().stream().distinct().count());
        }

        @Test
        @DisplayName("CS-TOS-08: DELETED es terminal — no permite ninguna transición posterior")
        void deletedEsTerminalNoPermiteTransicion() {
            String id = service.create(buildTournament("Torneo Deleted", 4)).getId();
            service.updateStatus(id, "DELETED");

            for (TournamentState next : TournamentState.values()) {
                assertThrows(TournamentException.class,
                        () -> service.updateStatus(id, next.name()),
                        "Debería fallar la transición DELETED → " + next);
            }
        }
    }
}
