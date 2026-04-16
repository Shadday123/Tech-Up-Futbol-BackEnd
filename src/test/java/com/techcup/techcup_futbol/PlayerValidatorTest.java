package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.validator.PlayerValidator;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerValidatorTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerValidator playerValidator;

    private StudentPlayer validPlayer;

    @BeforeEach
    void setUp() {
        validPlayer = new StudentPlayer();
        validPlayer.setId("J001");
        validPlayer.setFullname("Carlos Test");
        validPlayer.setAge(22);
        validPlayer.setNumberID(123456);
        validPlayer.setPosition(PositionEnum.Defender);
    }

    // ── validate (happy path) ────────────────────────────────────────────

    @Test
    void validate_withValidData_doesNotThrow() {
        when(playerRepository.existsByEmailIgnoreCase("carlos@mail.escuelaing.edu.co")).thenReturn(false);
        when(playerRepository.existsByNumberID(123456)).thenReturn(false);

        assertDoesNotThrow(() -> playerValidator.validate(validPlayer, "carlos@mail.escuelaing.edu.co"));
    }

    // ── validateFullname ─────────────────────────────────────────────────

    @Test
    void validate_withNullFullname_throwsException() {
        validPlayer.setFullname(null);

        PlayerException ex = assertThrows(PlayerException.class,
                () -> playerValidator.validate(validPlayer, "test@mail.escuelaing.edu.co"));
        assertEquals("fullname", ex.getField());
    }

    @Test
    void validate_withBlankFullname_throwsException() {
        validPlayer.setFullname("   ");

        PlayerException ex = assertThrows(PlayerException.class,
                () -> playerValidator.validate(validPlayer, "test@mail.escuelaing.edu.co"));
        assertEquals("fullname", ex.getField());
    }

    // ── validateAge ──────────────────────────────────────────────────────

    @Test
    void validate_withAgeTooLow_throwsException() {
        validPlayer.setAge(14);

        PlayerException ex = assertThrows(PlayerException.class,
                () -> playerValidator.validate(validPlayer, "test@mail.escuelaing.edu.co"));
        assertEquals("age", ex.getField());
    }

    @Test
    void validate_withAgeTooHigh_throwsException() {
        validPlayer.setAge(111);

        PlayerException ex = assertThrows(PlayerException.class,
                () -> playerValidator.validate(validPlayer, "test@mail.escuelaing.edu.co"));
        assertEquals("age", ex.getField());
    }

    // ── validateUniqueEmail ──────────────────────────────────────────────

    @Test
    void validate_withDuplicateEmail_throwsException() {
        when(playerRepository.existsByEmailIgnoreCase("carlos@mail.escuelaing.edu.co")).thenReturn(true);

        PlayerException ex = assertThrows(PlayerException.class,
                () -> playerValidator.validate(validPlayer, "carlos@mail.escuelaing.edu.co"));
        assertEquals("email", ex.getField());
        assertTrue(ex.getMessage().contains("carlos@mail.escuelaing.edu.co"));
    }

    // ── validateUniqueNumberID ───────────────────────────────────────────

    @Test
    void validate_withDuplicateNumberID_throwsException() {
        when(playerRepository.existsByEmailIgnoreCase("test@mail.escuelaing.edu.co")).thenReturn(false);
        when(playerRepository.existsByNumberID(123456)).thenReturn(true);

        PlayerException ex = assertThrows(PlayerException.class,
                () -> playerValidator.validate(validPlayer, "test@mail.escuelaing.edu.co"));
        assertEquals("numberID", ex.getField());
    }

    @Test
    void validate_withNullNumberID_throwsException() {
        validPlayer.setNumberID(0);
        // numberID is int (primitive), so null check applies via Integer wrapper
        // Test the standalone method with null
        PlayerException ex = assertThrows(PlayerException.class,
                () -> playerValidator.validateUniqueNumberID(null));
        assertEquals("numberID", ex.getField());
    }

    // ── static validators ────────────────────────────────────────────────

    @Test
    void validateDorsal_withValidValue_doesNotThrow() {
        assertDoesNotThrow(() -> PlayerValidator.validateDorsal(10));
    }

    @Test
    void validateDorsal_withZero_throwsException() {
        PlayerException ex = assertThrows(PlayerException.class,
                () -> PlayerValidator.validateDorsal(0));
        assertEquals("dorsal", ex.getField());
    }

    @Test
    void validateDorsal_withHundred_throwsException() {
        PlayerException ex = assertThrows(PlayerException.class,
                () -> PlayerValidator.validateDorsal(100));
        assertEquals("dorsal", ex.getField());
    }

    @Test
    void validateAge_withBoundaryValues_works() {
        assertDoesNotThrow(() -> PlayerValidator.validateAge(15));
        assertDoesNotThrow(() -> PlayerValidator.validateAge(110));
    }
}
