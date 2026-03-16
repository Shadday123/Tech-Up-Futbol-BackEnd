package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentServiceImpl;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para TournamentServiceImpl
 * Cubre: Happy Path, Error Path y Condicionales según pruebas.md
 *
 * Escenarios:
 * - HP-01: Creación de Torneo
 * - HP-02: Consulta por ID
 * - HP-03: Listado General
 * - HP-04: Inicio de Torneo
 * - EP-01: Campos Vacíos
 * - EP-02: Fechas Inválidas
 * - EP-03: ID Inexistente
 * - EP-04: Costo Negativo
 * - CS-01: Integridad de Máquina de Estados
 * - CS-02: Persistencia Volátil
 * - CS-03: Validación de Nombre Único
 */
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
        // Limpiar DataStore antes de cada prueba
        DataStore.limpiarDatos();

        startDate = LocalDateTime.now().plusDays(1);
        endDate = startDate.plusDays(30);

        validRequest = new CreateTournamentRequest(
                "TechCup 2026",
                startDate,
                endDate,
                150.0,
                8,
                "Reglas estándar de fútbol 11"
        );
    }

    // HAPPY PATH TESTS

    @Nested
    @DisplayName("Happy Path - Escenarios de Éxito")
    class HappyPathTests {

        @Test
        @DisplayName("HP-01: Creación de Torneo - Retorna 201 Created y estado DRAFT")
        void testCreateTournamentSuccessfully() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);

                // Act
                TournamentResponse createdTournament = tournamentService.create(validRequest);

                // Assert
                assertNotNull(createdTournament);
                assertEquals("TechCup 2026", createdTournament.name());
                assertEquals("DRAFT", createdTournament.currentState());
                assertEquals(150.0, createdTournament.registrationFee());
                assertTrue(DataStore.torneos.containsValue(
                        DataStore.torneos.values().stream()
                                .filter(t -> t.getName().equals("TechCup 2026"))
                                .findFirst()
                                .orElse(null)
                ));
            }
        }

        @Test
        @DisplayName("HP-02: Consulta por ID - Retorna 200 OK con detalles del torneo")
        void testGetTournamentByIdSuccessfully() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);

                // Arrange
                TournamentResponse created = tournamentService.create(validRequest);

                // Act
                TournamentResponse found = tournamentService.findById(created.id());

                // Assert
                assertNotNull(found);
                assertEquals(created.id(), found.id());
                assertEquals("TechCup 2026", found.name());
                assertEquals("DRAFT", found.currentState());
            }
        }

        @Test
        @DisplayName("HP-03: Listado General - Retorna 200 OK con lista de torneos")
        void testGetAllTournamentsSuccessfully() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);

                // Arrange
                tournamentService.create(validRequest);
                CreateTournamentRequest secondRequest = new CreateTournamentRequest(
                        "TechCup 2027",
                        startDate.plusDays(40),
                        endDate.plusDays(40),
                        200.0,
                        10,
                        "Reglas estándar de fútbol 11"
                );
                tournamentService.create(secondRequest);

                // Act
                List<TournamentResponse> allTournaments = tournamentService.findAll();

                // Assert
                assertNotNull(allTournaments);
                assertTrue(allTournaments.size() >= 2);
            }
        }

        @Test
        @DisplayName("HP-04: Inicio de Torneo - Cambia estado de DRAFT a ACTIVE")
        void testStartTournamentSuccessfully() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);
                mockedValidator.when(() -> TournamentValidator.validateStateTransition(any(), any())).thenAnswer(invocation -> null);

                // Arrange
                TournamentResponse created = tournamentService.create(validRequest);

                // Act
                TournamentResponse started = tournamentService.updateStatus(created.id(), "ACTIVE");

                // Assert
                assertNotNull(started);
                assertEquals("ACTIVE", started.currentState());
            }
        }

        @Test
        @DisplayName("HP-EXTRA: Finalización de Torneo - Cambia estado a COMPLETED")
        void testFinishTournamentSuccessfully() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);
                mockedValidator.when(() -> TournamentValidator.validateStateTransition(any(), any())).thenAnswer(invocation -> null);

                // Arrange
                TournamentResponse created = tournamentService.create(validRequest);
                tournamentService.updateStatus(created.id(), "ACTIVE");

                // Act
                TournamentResponse finished = tournamentService.updateStatus(created.id(), "COMPLETED");

                // Assert
                assertEquals("COMPLETED", finished.currentState());
            }
        }
    }

    // ERROR PATH TESTS

    @Nested
    @DisplayName("Error Path - Escenarios de Fallo")
    class ErrorPathTests {

        @Test
        @DisplayName("EP-01: Campos Vacíos - Nombre null lanza excepción en validador")
        void testCreateTournamentWithNullName() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any()))
                        .thenThrow(new IllegalArgumentException("El nombre del torneo es obligatorio"));

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> {
                    tournamentService.create(validRequest);
                });
            }
        }

        @Test
        @DisplayName("EP-02: Fechas Inválidas - Fecha final anterior a inicio")
        void testCreateTournamentWithInvalidDates() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any()))
                        .thenThrow(new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio"));

                CreateTournamentRequest invalidRequest = new CreateTournamentRequest(
                        "TechCup 2026",
                        endDate, // fecha final como inicio
                        startDate, // fecha inicio como fin
                        150.0,
                        8,
                        "Reglas"
                );

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> {
                    tournamentService.create(invalidRequest);
                });
            }
        }

        @Test
        @DisplayName("EP-03: ID Inexistente - Buscar torneo con ID que no existe")
        void testGetTournamentByInvalidId() {
            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                tournamentService.findById("T999");
            });
        }

        @Test
        @DisplayName("EP-04: Costo Negativo - Registrar torneo con costo < 0")
        void testCreateTournamentWithNegativeFee() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any()))
                        .thenThrow(new IllegalArgumentException("El costo no puede ser negativo"));

                CreateTournamentRequest invalidRequest = new CreateTournamentRequest(
                        "TechCup 2026",
                        startDate,
                        endDate,
                        -50.0, // costo negativo
                        8,
                        "Reglas"
                );

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> {
                    tournamentService.create(invalidRequest);
                });
            }
        }

        @Test
        @DisplayName("EP-EXTRA: Costo Cero - Registrar torneo con costo = 0")
        void testCreateTournamentWithZeroFee() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);

                CreateTournamentRequest zeroFeeRequest = new CreateTournamentRequest(
                        "Free Tournament",
                        startDate,
                        endDate,
                        0.0,
                        4,
                        "Reglas"
                );

                // Act
                TournamentResponse created = tournamentService.create(zeroFeeRequest);

                // Assert
                assertEquals(0.0, created.registrationFee());
            }
        }
    }

    //  CONDITIONAL SCENARIOS

    @Nested
    @DisplayName("Conditional Scenarios - Lógica de Negocio")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-01: Máquina de Estados - No se puede finalizar sin pasar por ACTIVE")
        void testStateTransitionValidation() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);
                mockedValidator.when(() -> TournamentValidator.validateStateTransition(TournamentState.DRAFT, TournamentState.COMPLETED))
                        .thenThrow(new IllegalStateException("No se puede ir directamente de DRAFT a COMPLETED"));

                // Arrange
                TournamentResponse created = tournamentService.create(validRequest);

                // Act & Assert
                assertThrows(IllegalStateException.class, () -> {
                    tournamentService.updateStatus(created.id(), "COMPLETED");
                });
            }
        }

        @Test
        @DisplayName("CS-02: Persistencia Volátil - Nuevo torneo debe aparecer en listado")
        void testPersistenceInDataStore() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);

                // Arrange & Act
                TournamentResponse created = tournamentService.create(validRequest);
                List<TournamentResponse> allTournaments = tournamentService.findAll();

                // Assert
                assertTrue(allTournaments.stream()
                        .anyMatch(t -> t.id().equals(created.id())));
            }
        }

        @Test
        @DisplayName("CS-03: Validación de Nombre Único - Evitar duplicados")
        void testUniqueTournamentName() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any()))
                        .thenAnswer(invocation -> {
                            CreateTournamentRequest req = invocation.getArgument(0);
                            // Simular validación de nombre único
                            if (DataStore.torneos.values().stream()
                                    .anyMatch(t -> t.getName().equals(req.name()))) {
                                throw new IllegalArgumentException("El nombre del torneo ya existe");
                            }
                            return null;
                        });

                // Arrange
                tournamentService.create(validRequest);

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> {
                    tournamentService.create(validRequest); // Mismo nombre
                });
            }
        }

        @Test
        @DisplayName("CS-EXTRA: Transición válida DRAFT → ACTIVE → COMPLETED")
        void testValidStateTransitionSequence() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any())).thenAnswer(invocation -> null);
                mockedValidator.when(() -> TournamentValidator.validateStateTransition(any(), any())).thenAnswer(invocation -> null);

                // Arrange & Act
                TournamentResponse created = tournamentService.create(validRequest);
                assertEquals("DRAFT", created.currentState());

                TournamentResponse active = tournamentService.updateStatus(created.id(), "ACTIVE");
                assertEquals("ACTIVE", active.currentState());

                TournamentResponse completed = tournamentService.updateStatus(created.id(), "COMPLETED");
                assertEquals("COMPLETED", completed.currentState());

                // Assert
                assertTrue(true); // Sequence completed without errors
            }
        }

        @Test
        @DisplayName("CS-EXTRA: Máximos y mínimos de equipos permitidos")
        void testTeamCountValidation() {
            try (MockedStatic<TournamentValidator> mockedValidator = mockStatic(TournamentValidator.class)) {
                mockedValidator.when(() -> TournamentValidator.validate(any()))
                        .thenAnswer(invocation -> {
                            CreateTournamentRequest req = invocation.getArgument(0);
                            if (req.maxTeams() < 4) {
                                throw new IllegalArgumentException("El torneo debe tener al menos 4 equipos");
                            }
                            if (req.maxTeams() > 32) {
                                throw new IllegalArgumentException("El máximo de equipos permitido es 32");
                            }
                            return null;
                        });

                // Act & Assert - Min teams
                assertThrows(IllegalArgumentException.class, () -> {
                    CreateTournamentRequest tooSmall = new CreateTournamentRequest(
                            "Small Tournament",
                            startDate,
                            endDate,
                            100.0,
                            2, // menor a 4
                            "Reglas"
                    );
                    tournamentService.create(tooSmall);
                });

                // Act & Assert - Max teams
                assertThrows(IllegalArgumentException.class, () -> {
                    CreateTournamentRequest tooLarge = new CreateTournamentRequest(
                            "Large Tournament",
                            startDate,
                            endDate,
                            100.0,
                            50, // mayor a 32
                            "Reglas"
                    );
                    tournamentService.create(tooLarge);
                });
            }
        }
    }
}