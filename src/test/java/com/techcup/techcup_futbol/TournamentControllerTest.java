package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.TournamentController;
import com.techcup.techcup_futbol.controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentControllerTest {

    @Mock TournamentService tournamentService;
    @InjectMocks TournamentController tournamentController;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        tournament.setId("T001");
        tournament.setName("Copa TechUp");
        tournament.setStartDate(LocalDateTime.of(2024, 6, 1, 9, 0));
        tournament.setEndDate(LocalDateTime.of(2024, 8, 31, 18, 0));
        tournament.setMaxTeams(8);
        tournament.setRegistrationFee(50000.0);
        tournament.setCurrentState(TournamentState.DRAFT);
    }

    @Test
    void create_validRequest_returnsCreated() {
        CreateTournamentRequest request = new CreateTournamentRequest(
                "Copa TechUp",
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 8, 31, 18, 0),
                50000.0, 8, "Reglas generales"
        );
        when(tournamentService.create(any(Tournament.class))).thenReturn(tournament);

        ResponseEntity<?> response = tournamentController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tournamentService).create(any(Tournament.class));
    }

    @Test
    void findAll_returnsOkWithList() {
        when(tournamentService.findAll()).thenReturn(List.of(tournament));

        ResponseEntity<?> response = tournamentController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tournamentService).findAll();
    }

    @Test
    void findById_returnsOk() {
        when(tournamentService.findById("T001")).thenReturn(tournament);

        ResponseEntity<?> response = tournamentController.findById("T001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tournamentService).findById("T001");
    }

    @Test
    void start_returnsOk() {
        tournament.setCurrentState(TournamentState.ACTIVE);
        when(tournamentService.updateStatus("T001", "ACTIVE")).thenReturn(tournament);

        ResponseEntity<?> response = tournamentController.start("T001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tournamentService).updateStatus("T001", "ACTIVE");
    }

    @Test
    void progress_returnsOk() {
        tournament.setCurrentState(TournamentState.IN_PROGRESS);
        when(tournamentService.updateStatus("T001", "IN_PROGRESS")).thenReturn(tournament);

        ResponseEntity<?> response = tournamentController.progress("T001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void finish_returnsOk() {
        tournament.setCurrentState(TournamentState.COMPLETED);
        when(tournamentService.updateStatus("T001", "COMPLETED")).thenReturn(tournament);

        ResponseEntity<?> response = tournamentController.finish("T001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void softDelete_returnsOk() {
        tournament.setCurrentState(TournamentState.DELETED);
        when(tournamentService.updateStatus("T001", "DELETED")).thenReturn(tournament);

        ResponseEntity<?> response = tournamentController.softDelete("T001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
