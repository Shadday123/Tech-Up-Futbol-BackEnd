package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.ResourceNotFoundException;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.TournamentEntity;
import com.techcup.techcup_futbol.persistence.mapper.TournamentPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.TournamentRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private TournamentEntity tournamentEntity;
    private Tournament validTournament;

    @BeforeEach
    void setUp() {
        validTournament = new Tournament();
        validTournament.setName("Copa Tech");
        validTournament.setStartDate(LocalDateTime.of(2026, 6, 1, 10, 0));
        validTournament.setEndDate(LocalDateTime.of(2026, 6, 30, 18, 0));
        validTournament.setRegistrationFee(100.0);
        validTournament.setMaxTeams(8);

        tournamentEntity = TournamentPersistenceMapper.toEntity(validTournament);
    }

    // ── CREATE ──

    @Test
    void create_withValidTournament_setsDraftAndSaves() {
        when(tournamentRepository.existsByName("Copa Tech")).thenReturn(false);
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournamentEntity);

        Tournament result = tournamentService.create(validTournament);

        assertNotNull(result.getId());
        assertEquals(TournamentState.DRAFT, result.getCurrentState());
        verify(tournamentRepository).save(any(TournamentEntity.class));
    }

    @Test
    void create_nameAlreadyExists_throwsTournamentException() {
        when(tournamentRepository.existsByName("Copa Tech")).thenReturn(true);

        TournamentException exception = assertThrows(TournamentException.class,
                () -> tournamentService.create(validTournament));

        assertEquals("name", exception.getField());
    }

    @Test
    void create_withNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> tournamentService.create(null));
    }

    // ── UPDATE STATUS ──

    @Test
    void updateStatus_validTransition_updatesState() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        tournamentEntity.setId("t-001");
        tournamentEntity.setCurrentState(TournamentState.DRAFT);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournamentEntity);

        Tournament result = tournamentService.updateStatus("t-001", "ACTIVE");

        assertEquals(TournamentState.ACTIVE, result.getCurrentState());
        verify(tournamentRepository).save(any(TournamentEntity.class));
    }

    @Test
    void updateStatus_invalidStateName_throwsException() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        tournamentEntity.setId("t-001");
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));

        TournamentException exception = assertThrows(TournamentException.class,
                () -> tournamentService.updateStatus("t-001", "INVALID_STATE"));

        assertEquals("state", exception.getField());
    }

    // ── FIND BY ID ──

    @Test
    void findById_existing_returnsTournament() {
        validTournament.setId("t-001");
        tournamentEntity.setId("t-001");
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));

        Tournament result = tournamentService.findById("t-001");

        assertEquals("t-001", result.getId());
        assertEquals("Copa Tech", result.getName());
    }

    @Test
    void findById_nonExistent_throwsResourceNotFoundException() {
        when(tournamentRepository.findById("t-999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> tournamentService.findById("t-999"));

        assertTrue(exception.getMessage().contains("t-999"));
    }

    // ── FIND ALL ──

    @Test
    void findAll_returnsList() {
        List<TournamentEntity> tournaments = List.of(tournamentEntity);
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentService.findAll();

        assertEquals(1, result.size());
        verify(tournamentRepository).findAll();
    }

    // ── NUEVOS MÉTODOS DEL REPOSITORY ──

    @Test
    void findByState_returnsList() {
        List<TournamentEntity> draftTournaments = List.of(tournamentEntity);
        when(tournamentRepository.findByCurrentState(TournamentState.DRAFT)).thenReturn(draftTournaments);

        List<Tournament> result = tournamentService.findByState(TournamentState.DRAFT);

        assertEquals(1, result.size());
        verify(tournamentRepository).findByCurrentState(TournamentState.DRAFT);
    }

    @Test
    void findByName_returnsList() {
        List<TournamentEntity> copaTournaments = List.of(tournamentEntity);
        when(tournamentRepository.findByName("Copa Tech")).thenReturn(copaTournaments);

        List<Tournament> result = tournamentService.findByName("Copa Tech");

        assertEquals(1, result.size());
        verify(tournamentRepository).findByName("Copa Tech");
    }

    @Test
    void existsByName_returnsTrue() {
        when(tournamentRepository.existsByName("Copa Tech")).thenReturn(true);

        boolean result = tournamentService.existsByName("Copa Tech");

        assertTrue(result);
        verify(tournamentRepository).existsByName("Copa Tech");
    }

    @Test
    void existsByName_returnsFalse() {
        when(tournamentRepository.existsByName("Otro Torneo")).thenReturn(false);

        boolean result = tournamentService.existsByName("Otro Torneo");

        assertFalse(result);
        verify(tournamentRepository).existsByName("Otro Torneo");
    }

    // ── CREATE OR UPDATE CONFIG ──

    @Test
    void createOrUpdateConfig_validDraft_setsConfig() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        validTournament.setConfigId(null);
        tournamentEntity.setId("t-001");
        tournamentEntity.setCurrentState(TournamentState.DRAFT);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournamentEntity);

        LocalDateTime deadline = LocalDateTime.of(2026, 5, 20, 10, 0);

        Tournament result = tournamentService.createOrUpdateConfig(
                "t-001", "Reglas oficiales", deadline,
                List.of("2026-05-15"), List.of("Lunes 10:00"),
                List.of("Cancha A"), "Tarjeta roja = expulsion");

        assertNotNull(result.getConfigId());
        verify(tournamentRepository).save(any(TournamentEntity.class));
    }

    @Test
    void createOrUpdateConfig_inProgress_throwsException() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.IN_PROGRESS);
        tournamentEntity.setId("t-001");
        tournamentEntity.setCurrentState(TournamentState.IN_PROGRESS);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));

        TournamentException exception = assertThrows(TournamentException.class,
                () -> tournamentService.createOrUpdateConfig(
                        "t-001", "Rules", null, null, null, null, null));

        assertEquals("state", exception.getField());
    }

    @Test
    void createOrUpdateConfig_invalidDeadline_throwsException() {
        validTournament.setId("t-001");
        validTournament.setCurrentState(TournamentState.DRAFT);
        tournamentEntity.setId("t-001");
        tournamentEntity.setCurrentState(TournamentState.DRAFT);
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));

        LocalDateTime deadlineAfterStart = LocalDateTime.of(2026, 7, 1, 10, 0);

        TournamentException exception = assertThrows(TournamentException.class,
                () -> tournamentService.createOrUpdateConfig(
                        "t-001", "Rules", deadlineAfterStart,
                        null, null, null, null));

        assertEquals("registrationDeadline", exception.getField());
    }

    // ── FIND CONFIG ──

    @Test
    void findConfig_withConfig_returnsTournament() {
        validTournament.setId("t-001");
        validTournament.setConfigId("cfg-001");
        tournamentEntity.setId("t-001");
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));

        Tournament result = tournamentService.findConfig("t-001");

        assertNotNull(result.getConfigId());
    }

    @Test
    void findConfig_withoutConfig_throwsException() {
        validTournament.setId("t-001");
        validTournament.setConfigId(null);
        tournamentEntity.setId("t-001");
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));

        TournamentException exception = assertThrows(TournamentException.class,
                () -> tournamentService.findConfig("t-001"));

        assertEquals("config", exception.getField());
    }
}

