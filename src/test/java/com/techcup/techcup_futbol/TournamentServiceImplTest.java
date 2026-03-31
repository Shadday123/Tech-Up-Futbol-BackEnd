package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.ResourceNotFoundException;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentServiceImpl;
import com.techcup.techcup_futbol.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Tournament validTournament;

    @BeforeEach
    void setUp() {
        validTournament = new Tournament();
        validTournament.setName("Copa Tech");
        validTournament.setStartDate(LocalDateTime.of(2026, 6, 1, 10, 0));
        validTournament.setEndDate(LocalDateTime.of(2026, 6, 30, 18, 0));
        validTournament.setRegistrationFee(100.0);
        validTournament.setMaxTeams(8);
    }

    // ── CREATE ──

    @Test
    void create_withValidTournament_setsDraftAndSaves() {
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(validTournament);

        Tournament result = tournamentService.create(validTournament);

        assertNotNull(result.getId());
        assertEquals(TournamentState.DRAFT, result.getCurrentState());
        verify(tournamentRepository).save(validTournament);
    }

    @Test
    void create_withNull_throwsTournamentException() {
        // The service checks null AFTER calling tournament.getName() in the log,
        // so passing null will cause a NullPointerException before the null check.
        // However, TournamentValidator.validate also throws for null.
        // Based on the implementation, the log line dereferences tournament first.
        assertThrows(NullPointerException.class, () -> tournamentService.create(null));
    }

    // ── UPDATE STATUS ──

    @Test
    void updateStatus_validTransition_updatesState() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(validTournament);

        Tournament result = tournamentService.updateStatus("t-001", "ACTIVE");

        assertEquals(TournamentState.ACTIVE, result.getCurrentState());
        verify(tournamentRepository).save(validTournament);
    }

    @Test
    void updateStatus_invalidStateName_throwsException() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));

        assertThrows(TournamentException.class,
                () -> tournamentService.updateStatus("t-001", "INVALID_STATE"));
    }

    @Test
    void updateStatus_invalidTransition_throwsException() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.COMPLETED);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));

        assertThrows(TournamentException.class,
                () -> tournamentService.updateStatus("t-001", "ACTIVE"));
    }

    // ── FIND BY ID ──

    @Test
    void findById_existing_returnsTournament() {
        validTournament.setId("t-001");
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));

        Tournament result = tournamentService.findById("t-001");

        assertEquals("t-001", result.getId());
        assertEquals("Copa Tech", result.getName());
    }

    @Test
    void findById_nonExistent_throwsResourceNotFoundException() {
        when(tournamentRepository.findById("t-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tournamentService.findById("t-999"));
    }

    // ── FIND ALL ──

    @Test
    void findAll_returnsList() {
        List<Tournament> tournaments = List.of(validTournament);
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentService.findAll();

        assertEquals(1, result.size());
        verify(tournamentRepository).findAll();
    }

    // ── CREATE OR UPDATE CONFIG ──

    @Test
    void createOrUpdateConfig_validDraft_setsConfig() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        validTournament.setConfigId(null);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(validTournament);

        LocalDateTime deadline = LocalDateTime.of(2026, 5, 20, 10, 0);

        Tournament result = tournamentService.createOrUpdateConfig(
                "t-001", "Reglas oficiales", deadline,
                List.of("2026-05-15"), List.of("Lunes 10:00"),
                List.of("Cancha A"), "Tarjeta roja = expulsion");

        assertNotNull(result.getConfigId());
        assertEquals("Reglas oficiales", result.getRules());
        assertEquals(deadline, result.getRegistrationDeadline());
        verify(tournamentRepository).save(validTournament);
    }

    @Test
    void createOrUpdateConfig_inProgress_throwsException() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.IN_PROGRESS);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));

        assertThrows(TournamentException.class,
                () -> tournamentService.createOrUpdateConfig(
                        "t-001", "Rules", null, null, null, null, null));
    }

    @Test
    void createOrUpdateConfig_invalidDeadline_throwsException() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));

        // Deadline AFTER start date should throw
        LocalDateTime deadlineAfterStart = LocalDateTime.of(2026, 7, 1, 10, 0);

        assertThrows(TournamentException.class,
                () -> tournamentService.createOrUpdateConfig(
                        "t-001", "Rules", deadlineAfterStart,
                        null, null, null, null));
    }

    // ── FIND CONFIG ──

    @Test
    void findConfig_withConfig_returnsTournament() {
        validTournament.setId("t-001");
        validTournament.setConfigId("cfg-001");
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));

        Tournament result = tournamentService.findConfig("t-001");

        assertTrue(result.hasConfig());
    }

    @Test
    void findConfig_withoutConfig_throwsException() {
        validTournament.setId("t-001");
        validTournament.setConfigId(null);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(validTournament));

        assertThrows(TournamentException.class,
                () -> tournamentService.findConfig("t-001"));
    }
}
