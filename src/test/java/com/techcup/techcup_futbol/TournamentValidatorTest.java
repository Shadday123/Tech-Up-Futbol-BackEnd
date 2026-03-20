package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TournamentValidator Tests")
class TournamentValidatorTest {

    private static final LocalDateTime FUTURE_START = LocalDateTime.now().plusDays(5);
    private static final LocalDateTime FUTURE_END   = LocalDateTime.now().plusDays(30);

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TOV-01: validate() con request válido no lanza excepción")
        void requestValidoNoLanzaExcepcion() {
            CreateTournamentRequest req = new CreateTournamentRequest(
                    "TechCup 2026", FUTURE_START, FUTURE_END, 150.0, 8, "Reglas estándar");
            assertDoesNotThrow(() -> TournamentValidator.validate(req));
        }

        @Test
        @DisplayName("HP-TOV-02: validateName con nombre válido no lanza excepción")
        void nombreValidoNoLanzaExcepcion() {
            assertDoesNotThrow(() -> TournamentValidator.validateName("Torneo Primavera 2026"));
        }

        @Test
        @DisplayName("HP-TOV-03: validateDates con end posterior a start no lanza excepción")
        void fechasValidasNoLanzaExcepcion() {
            assertDoesNotThrow(() ->
                    TournamentValidator.validateDates(
                            LocalDate.now().plusDays(1),
                            LocalDate.now().plusDays(30)));
        }

        @Test
        @DisplayName("HP-TOV-04: validateRegistrationFee con 0 no lanza excepción")
        void cuotaCeroNoLanzaExcepcion() {
            assertDoesNotThrow(() -> TournamentValidator.validateRegistrationFee(0.0));
        }

        @Test
        @DisplayName("HP-TOV-05: validateRegistrationFee con valor positivo no lanza excepción")
        void cuotaPositivaNoLanzaExcepcion() {
            assertDoesNotThrow(() -> TournamentValidator.validateRegistrationFee(500.0));
        }

        @Test
        @DisplayName("HP-TOV-06: validateMaxTeams con 2 (mínimo) no lanza excepción")
        void maxTeamsMinimoNoLanzaExcepcion() {
            assertDoesNotThrow(() -> TournamentValidator.validateMaxTeams(2));
        }

        @Test
        @DisplayName("HP-TOV-07: validateMaxTeams con 32 no lanza excepción")
        void maxTeams32NoLanzaExcepcion() {
            assertDoesNotThrow(() -> TournamentValidator.validateMaxTeams(32));
        }

        @Test
        @DisplayName("HP-TOV-08: DRAFT → ACTIVE transición permitida")
        void draftAActivePermitida() {
            assertDoesNotThrow(() ->
                    TournamentValidator.validateStateTransition(TournamentState.DRAFT, TournamentState.ACTIVE));
        }

        @Test
        @DisplayName("HP-TOV-09: DRAFT → DELETED transición permitida")
        void draftADeletedPermitida() {
            assertDoesNotThrow(() ->
                    TournamentValidator.validateStateTransition(TournamentState.DRAFT, TournamentState.DELETED));
        }

        @Test
        @DisplayName("HP-TOV-10: ACTIVE → IN_PROGRESS transición permitida")
        void activeAInProgressPermitida() {
            assertDoesNotThrow(() ->
                    TournamentValidator.validateStateTransition(TournamentState.ACTIVE, TournamentState.IN_PROGRESS));
        }

        @Test
        @DisplayName("HP-TOV-11: ACTIVE → DELETED transición permitida")
        void activeADeletedPermitida() {
            assertDoesNotThrow(() ->
                    TournamentValidator.validateStateTransition(TournamentState.ACTIVE, TournamentState.DELETED));
        }

        @Test
        @DisplayName("HP-TOV-12: IN_PROGRESS → COMPLETED transición permitida")
        void inProgressACompletedPermitida() {
            assertDoesNotThrow(() ->
                    TournamentValidator.validateStateTransition(TournamentState.IN_PROGRESS, TournamentState.COMPLETED));
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TOV-01: validate() con request null lanza TournamentException")
        void requestNullLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validate(null));
            assertEquals(TournamentException.REQUEST_NULL, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TOV-02: validateName con null lanza TournamentException")
        void nombreNullLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateName(null));
            assertEquals("name", ex.getField());
            assertEquals(TournamentException.NAME_EMPTY, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TOV-03: validateName con vacío lanza TournamentException")
        void nombreVacioLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateName(""));
            assertEquals(TournamentException.NAME_EMPTY, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TOV-04: validateName con solo espacios lanza TournamentException")
        void nombreEspaciosLanzaExcepcion() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateName("   "));
        }

        @Test
        @DisplayName("EP-TOV-05: validateDates con null en startDate lanza TournamentException")
        void startDateNullLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateDates(null, LocalDate.now().plusDays(10)));
            assertEquals("dates", ex.getField());
            assertEquals(TournamentException.DATES_NULL, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TOV-06: validateDates con null en endDate lanza TournamentException")
        void endDateNullLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateDates(LocalDate.now().plusDays(1), null));
            assertEquals(TournamentException.DATES_NULL, ex.getMessage());
        }

        @Test
        @DisplayName("EP-TOV-07: validateDates con end == start lanza TournamentException")
        void endIgualStartLanzaExcepcion() {
            LocalDate same = LocalDate.now().plusDays(5);
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateDates(same, same));
            assertEquals("dates", ex.getField());
            assertTrue(ex.getMessage().contains(same.toString()));
        }

        @Test
        @DisplayName("EP-TOV-08: validateDates con end anterior a start lanza TournamentException")
        void endAnteriorStartLanzaExcepcion() {
            LocalDate start = LocalDate.now().plusDays(10);
            LocalDate end   = LocalDate.now().plusDays(5);
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateDates(start, end));
            assertEquals("dates", ex.getField());
        }

        @Test
        @DisplayName("EP-TOV-09: validateRegistrationFee con valor negativo lanza TournamentException")
        void cuotaNegativaLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateRegistrationFee(-1.0));
            assertEquals("registrationFee", ex.getField());
            assertTrue(ex.getMessage().contains("-1"));
        }

        @Test
        @DisplayName("EP-TOV-10: validateRegistrationFee con -0.01 lanza TournamentException")
        void cuotaDecimalNegativaLanzaExcepcion() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateRegistrationFee(-0.01));
        }

        @Test
        @DisplayName("EP-TOV-11: validateMaxTeams con 1 lanza TournamentException")
        void maxTeamsUnoLanzaExcepcion() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateMaxTeams(1));
            assertEquals("maxTeams", ex.getField());
            assertTrue(ex.getMessage().contains("1"));
        }

        @Test
        @DisplayName("EP-TOV-12: validateMaxTeams con 0 lanza TournamentException")
        void maxTeamsCeroLanzaExcepcion() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateMaxTeams(0));
        }

        @Test
        @DisplayName("EP-TOV-13: validateMaxTeams negativo lanza TournamentException")
        void maxTeamsNegativoLanzaExcepcion() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateMaxTeams(-4));
        }

        // ── Transiciones inválidas

        @Test
        @DisplayName("EP-TOV-14: DRAFT → IN_PROGRESS (saltar ACTIVE) lanza TournamentException")
        void draftAInProgressProhibida() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.DRAFT, TournamentState.IN_PROGRESS));
            assertEquals("state", ex.getField());
            assertTrue(ex.getMessage().contains("DRAFT"));
            assertTrue(ex.getMessage().contains("IN_PROGRESS"));
        }

        @Test
        @DisplayName("EP-TOV-15: DRAFT → COMPLETED (saltar estados) lanza TournamentException")
        void draftACompletedProhibida() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.DRAFT, TournamentState.COMPLETED));
        }

        @Test
        @DisplayName("EP-TOV-16: ACTIVE → COMPLETED (saltar IN_PROGRESS) lanza TournamentException")
        void activeACompletedProhibida() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.ACTIVE, TournamentState.COMPLETED));
        }

        @Test
        @DisplayName("EP-TOV-17: ACTIVE → DRAFT (retroceder) lanza TournamentException")
        void activeADraftRetrocederProhibido() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.ACTIVE, TournamentState.DRAFT));
        }

        @Test
        @DisplayName("EP-TOV-18: IN_PROGRESS → ACTIVE (retroceder) lanza TournamentException")
        void inProgressAActiveRetrocederProhibido() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.IN_PROGRESS, TournamentState.ACTIVE));
        }

        @Test
        @DisplayName("EP-TOV-19: IN_PROGRESS → DRAFT (retroceder) lanza TournamentException")
        void inProgressADraftRetrocederProhibido() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.IN_PROGRESS, TournamentState.DRAFT));
        }

        @Test
        @DisplayName("EP-TOV-20: IN_PROGRESS → DELETED no permitido")
        void inProgressADeletedProhibido() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.IN_PROGRESS, TournamentState.DELETED));
        }

        @Test
        @DisplayName("EP-TOV-21: COMPLETED → cualquier estado lanza TournamentException")
        void completedADraftProhibido() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.COMPLETED, TournamentState.DRAFT));
        }

        @Test
        @DisplayName("EP-TOV-22: COMPLETED → ACTIVE lanza TournamentException")
        void completedAActiveProhibido() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.COMPLETED, TournamentState.ACTIVE));
        }

        @Test
        @DisplayName("EP-TOV-23: DELETED → cualquier estado lanza TournamentException")
        void deletedAActiveProhibido() {
            assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.DELETED, TournamentState.ACTIVE));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TOV-01: validate() completo falla si nombre vacío — campo correcto")
        void validacionCompletaNombreVacioFallaCampoCorrect() {
            CreateTournamentRequest req = new CreateTournamentRequest(
                    "", FUTURE_START, FUTURE_END, 150.0, 8, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validate(req));
            assertEquals("name", ex.getField());
        }

        @Test
        @DisplayName("CS-TOV-02: validate() completo falla si fechas inválidas")
        void validacionCompletaFechasInvalidas() {
            CreateTournamentRequest req = new CreateTournamentRequest(
                    "Torneo Test", FUTURE_END, FUTURE_START, 150.0, 8, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validate(req));
            assertEquals("dates", ex.getField());
        }

        @Test
        @DisplayName("CS-TOV-03: validate() completo falla si cuota negativa")
        void validacionCompletaCuotaNegativa() {
            CreateTournamentRequest req = new CreateTournamentRequest(
                    "Torneo Test", FUTURE_START, FUTURE_END, -10.0, 8, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validate(req));
            assertEquals("registrationFee", ex.getField());
        }

        @Test
        @DisplayName("CS-TOV-04: validate() completo falla si maxTeams < 2")
        void validacionCompletaMaxTeamsBajo() {
            CreateTournamentRequest req = new CreateTournamentRequest(
                    "Torneo Test", FUTURE_START, FUTURE_END, 100.0, 1, "Reglas");
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validate(req));
            assertEquals("maxTeams", ex.getField());
        }

        @Test
        @DisplayName("CS-TOV-05: Flujo completo de estados válidos DRAFT→ACTIVE→IN_PROGRESS→COMPLETED")
        void flujoCompletoEstadosValidos() {
            assertDoesNotThrow(() -> {
                TournamentValidator.validateStateTransition(TournamentState.DRAFT, TournamentState.ACTIVE);
                TournamentValidator.validateStateTransition(TournamentState.ACTIVE, TournamentState.IN_PROGRESS);
                TournamentValidator.validateStateTransition(TournamentState.IN_PROGRESS, TournamentState.COMPLETED);
            });
        }

        @Test
        @DisplayName("CS-TOV-06: validateDates con diferencia de un día exacto es válido")
        void diferenciaDiaExactoValido() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end   = LocalDate.now().plusDays(2);
            assertDoesNotThrow(() -> TournamentValidator.validateDates(start, end));
        }

        @Test
        @DisplayName("CS-TOV-07: validateStateTransition mensaje contiene ambos estados")
        void mensajeContieneAmbosEstados() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> TournamentValidator.validateStateTransition(
                            TournamentState.COMPLETED, TournamentState.DRAFT));
            assertTrue(ex.getMessage().contains("COMPLETED"));
            assertTrue(ex.getMessage().contains("DRAFT"));
        }

        @Test
        @DisplayName("CS-TOV-08: cuota exactamente 0 es el límite válido inferior")
        void cuotaCeroEsLimiteValido() {
            assertDoesNotThrow(() -> TournamentValidator.validateRegistrationFee(0.0));
        }

        @Test
        @DisplayName("CS-TOV-09: maxTeams exactamente 2 es el límite válido inferior")
        void maxTeamsDosEsLimiteValido() {
            assertDoesNotThrow(() -> TournamentValidator.validateMaxTeams(2));
        }

        @Test
        @DisplayName("CS-TOV-10: DELETED es un estado terminal — ninguna transición válida")
        void deletedEsTerminal() {
            for (TournamentState next : TournamentState.values()) {
                assertThrows(TournamentException.class,
                        () -> TournamentValidator.validateStateTransition(TournamentState.DELETED, next),
                        "Debería fallar la transición DELETED → " + next);
            }
        }

        @Test
        @DisplayName("CS-TOV-11: COMPLETED es un estado terminal — ninguna transición válida")
        void completedEsTerminal() {
            for (TournamentState next : TournamentState.values()) {
                assertThrows(TournamentException.class,
                        () -> TournamentValidator.validateStateTransition(TournamentState.COMPLETED, next),
                        "Debería fallar la transición COMPLETED → " + next);
            }
        }
    }
}
