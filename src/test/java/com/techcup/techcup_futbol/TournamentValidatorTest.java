package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TournamentValidatorTest {

    private Tournament validTournament;

    @BeforeEach
    void setUp() {
        validTournament = new Tournament();
        validTournament.setName("Torneo Sistemas");
        validTournament.setStartDate(LocalDateTime.of(2026, 4 ,18, 9 ,00));
        validTournament.setEndDate(LocalDateTime.of(2026, 4, 19, 16, 00));
        validTournament.setRegistrationFee(110000.0);
        validTournament.setMaxTeams(8);
    }

    @Test
    void validate_withValidTournament_doesNotThrow() {
        assertDoesNotThrow(() -> TournamentValidator.validate(validTournament));
    }

    @Test
    void validateName_withValidName_doesNotThrow() {
        assertDoesNotThrow(() -> TournamentValidator.validateName("Torneo Valido"));
    }

    @Test
    void validate_withNullTournament_throwsException() {
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validate(null));
        assertFalse(ex.getMessage().contains("null"));
    }

    @Test
    void validateName_withNullName_throwsException() {
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validateName(null));
        assertEquals("name", ex.getField());
    }

    @Test
    void validateName_withBlankName_throwsException() {
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validateName("   "));
        assertEquals("name", ex.getField());
    }

    @Test
    void validateDates_endBeforeStart_throwsException() {
        validTournament.setEndDate(LocalDateTime.of(2026, 04, 18, 16, 00));
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validate(validTournament));
        assertEquals("dates", ex.getField());
    }

    @Test
    void validateDates_nullDates_throwsException() {
        Tournament tournament = new Tournament();
        tournament.setStartDate(null);
        tournament.setEndDate(null);
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validateDates(null, null));
        assertEquals("dates", ex.getField());
    }

    @Test
    void validateRegistrationFee_negative_throwsException() {
        validTournament.setRegistrationFee(-1000.0);
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validate(validTournament));
        assertEquals("registrationFee", ex.getField());
    }

    @Test
    void validateMaxTeams_tooLow_throwsException() {
        validTournament.setMaxTeams(1);
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validate(validTournament));
        assertEquals("maxTeams", ex.getField());
    }

    @Test
    void validateMaxTeams_oddNumber_throwsException() {
        validTournament.setMaxTeams(7);
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validate(validTournament));
        assertEquals("maxTeams", ex.getField());
    }

    @Test
    void validateStateTransition_validTransitions_doesNotThrow() {
        assertDoesNotThrow(() -> TournamentValidator.validateStateTransition(TournamentState.DRAFT, TournamentState.ACTIVE));
        assertDoesNotThrow(() -> TournamentValidator.validateStateTransition(TournamentState.ACTIVE, TournamentState.IN_PROGRESS));
        assertDoesNotThrow(() -> TournamentValidator.validateStateTransition(TournamentState.IN_PROGRESS, TournamentState.COMPLETED));
    }

    @Test
    void validateStateTransition_invalidTransition_throwsException() {
        TournamentException ex = assertThrows(TournamentException.class,
                () -> TournamentValidator.validateStateTransition(TournamentState.COMPLETED, TournamentState.ACTIVE));
        assertEquals("state", ex.getField());
    }
}