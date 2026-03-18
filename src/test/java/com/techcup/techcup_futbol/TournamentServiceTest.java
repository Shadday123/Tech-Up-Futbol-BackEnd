package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentServiceImpl;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import com.techcup.techcup_futbol.exception.TournamentException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tournament Service Tests")
class TournamentServiceTest {

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private CreateTournamentRequest validRequest;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();

        startDate = LocalDateTime.now().plusDays(1);
        endDate   = startDate.plusDays(30);

        validRequest = new CreateTournamentRequest(
                "TechCup 2026",
                startDate,
                endDate,
                150.0,
                8,
                "Reglas estándar de fútbol 11"
        );
    }

    // ── Happy Path ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Happy Path")
    class HappyPathTests {

        @Test
        @DisplayName("HP-01: Crear torneo retorna estado DRAFT")
        void testCreateTournamentSuccessfully() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);

                TournamentResponse created = tournamentService.create(validRequest);

                assertNotNull(created);
                assertEquals("TechCup 2026", created.name());
                assertEquals("DRAFT", created.currentState());
                assertEquals(150.0, created.registrationFee());
                assertTrue(DataStore.torneos.values().stream()
                        .anyMatch(t -> t.getName().equals("TechCup 2026")));
            }
        }

        @Test
        @DisplayName("HP-02: Consultar torneo por ID existente")
        void testGetTournamentByIdSuccessfully() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);

                TournamentResponse created = tournamentService.create(validRequest);
                TournamentResponse found   = tournamentService.findById(created.id());

                assertNotNull(found);
                assertEquals(created.id(), found.id());
                assertEquals("TechCup 2026", found.name());
            }
        }

        @Test
        @DisplayName("HP-03: Listar torneos retorna todos los creados")
        void testGetAllTournamentsSuccessfully() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);

                tournamentService.create(validRequest);
                tournamentService.create(new CreateTournamentRequest(
                        "TechCup 2027",
                        startDate.plusDays(40),
                        endDate.plusDays(40),
                        200.0, 10, "Reglas"
                ));

                List<TournamentResponse> all = tournamentService.findAll();

                assertNotNull(all);
                assertEquals(2, all.size());
            }
        }

        @Test
        @DisplayName("HP-04: Cambiar estado DRAFT → ACTIVE")
        void testStartTournamentSuccessfully() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);
                mv.when(() -> TournamentValidator.validateStateTransition(any(), any())).thenAnswer(i -> null);

                TournamentResponse created = tournamentService.create(validRequest);
                TournamentResponse started = tournamentService.updateStatus(created.id(), "ACTIVE");

                assertEquals("ACTIVE", started.currentState());
            }
        }

        @Test
        @DisplayName("HP-05: Flujo completo DRAFT → ACTIVE → IN_PROGRESS → COMPLETED")
        void testFullStateTransitionSequence() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);
                mv.when(() -> TournamentValidator.validateStateTransition(any(), any())).thenAnswer(i -> null);

                TournamentResponse t = tournamentService.create(validRequest);
                assertEquals("ACTIVE",
                        tournamentService.updateStatus(t.id(), "ACTIVE").currentState());
                assertEquals("IN_PROGRESS",
                        tournamentService.updateStatus(t.id(), "IN_PROGRESS").currentState());
                assertEquals("COMPLETED",
                        tournamentService.updateStatus(t.id(), "COMPLETED").currentState());
            }
        }

        @Test
        @DisplayName("HP-06: Crear torneo con costo 0 es permitido")
        void testCreateTournamentWithZeroFee() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);

                TournamentResponse created = tournamentService.create(new CreateTournamentRequest(
                        "Free Tournament", startDate, endDate, 0.0, 4, "Reglas"
                ));

                assertEquals(0.0, created.registrationFee());
            }
        }
    }

    // ── Error Path ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Error Path")
    class ErrorPathTests {

        @Test
        @DisplayName("EP-01: Nombre nulo lanza TournamentException")
        void testCreateWithNullName() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any()))
                        .thenThrow(new TournamentException("name", TournamentException.NAME_EMPTY));

                assertThrows(TournamentException.class, () ->
                        tournamentService.create(validRequest)
                );
            }
        }

        @Test
        @DisplayName("EP-02: Fechas inválidas lanzan TournamentException")
        void testCreateWithInvalidDates() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any()))
                        .thenThrow(new TournamentException("dates",
                                String.format(TournamentException.END_DATE_NOT_AFTER_START,
                                        endDate, startDate)));

                assertThrows(TournamentException.class, () ->
                        tournamentService.create(new CreateTournamentRequest(
                                "TechCup", endDate, startDate, 150.0, 8, "Reglas"
                        ))
                );
            }
        }

        @Test
        @DisplayName("EP-03: ID inexistente en findById lanza TournamentException")
        void testGetByInvalidId() {
            assertThrows(TournamentException.class, () ->
                    tournamentService.findById("T999")
            );
        }

        @Test
        @DisplayName("EP-04: Costo negativo lanza TournamentException")
        void testCreateWithNegativeFee() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any()))
                        .thenThrow(new TournamentException("registrationFee",
                                String.format(TournamentException.REGISTRATION_FEE_NEGATIVE, -50.0)));

                assertThrows(TournamentException.class, () ->
                        tournamentService.create(new CreateTournamentRequest(
                                "TechCup", startDate, endDate, -50.0, 8, "Reglas"
                        ))
                );
            }
        }

        @Test
        @DisplayName("EP-05: Estado inválido en updateStatus lanza TournamentException")
        void testUpdateStatusWithInvalidState() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);

                TournamentResponse created = tournamentService.create(validRequest);

                assertThrows(TournamentException.class, () ->
                        tournamentService.updateStatus(created.id(), "ESTADO_INVALIDO")
                );
            }
        }

        @Test
        @DisplayName("EP-06: ID inexistente en updateStatus lanza TournamentException")
        void testUpdateStatusWithInvalidId() {
            assertThrows(TournamentException.class, () ->
                    tournamentService.updateStatus("T999", "ACTIVE")
            );
        }
    }

    // ── Conditional ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-01: Transición DRAFT → COMPLETED no permitida lanza TournamentException")
        void testInvalidStateTransition() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);
                mv.when(() -> TournamentValidator.validateStateTransition(
                                TournamentState.DRAFT, TournamentState.COMPLETED))
                        .thenThrow(new TournamentException("state",
                                String.format(TournamentException.INVALID_STATE_TRANSITION,
                                        TournamentState.DRAFT, TournamentState.COMPLETED)));

                TournamentResponse created = tournamentService.create(validRequest);

                assertThrows(TournamentException.class, () ->
                        tournamentService.updateStatus(created.id(), "COMPLETED")
                );
            }
        }

        @Test
        @DisplayName("CS-02: Torneo creado aparece en findAll")
        void testNewTournamentAppearsInFindAll() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);

                TournamentResponse created = tournamentService.create(validRequest);

                assertTrue(tournamentService.findAll().stream()
                        .anyMatch(t -> t.id().equals(created.id())));
            }
        }

        @Test
        @DisplayName("CS-03: Nombre duplicado es rechazado por el validator")
        void testDuplicateTournamentName() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any()))
                        .thenAnswer(invocation -> {
                            CreateTournamentRequest req = invocation.getArgument(0);
                            if (DataStore.torneos.values().stream()
                                    .anyMatch(t -> t.getName().equals(req.name()))) {
                                throw new TournamentException("name", TournamentException.NAME_EMPTY);
                            }
                            return null;
                        });

                tournamentService.create(validRequest);

                assertThrows(TournamentException.class, () ->
                        tournamentService.create(validRequest)
                );
            }
        }

        @Test
        @DisplayName("CS-04: IDs generados son únicos")
        void testUniqueIdGeneration() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any())).thenAnswer(i -> null);

                TournamentResponse t1 = tournamentService.create(validRequest);
                TournamentResponse t2 = tournamentService.create(new CreateTournamentRequest(
                        "TechCup 2027",
                        startDate.plusDays(40),
                        endDate.plusDays(40),
                        100.0, 4, "Reglas"
                ));

                assertNotEquals(t1.id(), t2.id());
            }
        }

        @Test
        @DisplayName("CS-05: maxTeams mínimo es validado por el validator")
        void testMinTeamsValidation() {
            try (MockedStatic<TournamentValidator> mv = mockStatic(TournamentValidator.class)) {
                mv.when(() -> TournamentValidator.validate(any()))
                        .thenAnswer(invocation -> {
                            CreateTournamentRequest req = invocation.getArgument(0);
                            if (req.maxTeams() < 2) {
                                throw new TournamentException("maxTeams",
                                        String.format(TournamentException.MAX_TEAMS_TOO_LOW,
                                                req.maxTeams()));
                            }
                            return null;
                        });

                assertThrows(TournamentException.class, () ->
                        tournamentService.create(new CreateTournamentRequest(
                                "Mini", startDate, endDate, 100.0, 1, "Reglas"
                        ))
                );
            }
        }
    }
}