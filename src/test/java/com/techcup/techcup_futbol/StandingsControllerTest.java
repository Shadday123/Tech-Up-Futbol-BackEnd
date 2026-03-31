package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.StandingsController;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.StandingsService;
import com.techcup.techcup_futbol.core.service.TournamentService;
import com.techcup.techcup_futbol.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandingsControllerTest {

    @Mock
    private StandingsService standingsService;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private StandingsController standingsController;

    private Team buildTeam(String id, String name) {
        Team team = new Team();
        team.setId(id);
        team.setTeamName(name);
        team.setShieldUrl("shield.png");
        return team;
    }

    private Tournament buildTournament(String id, String name) {
        Tournament t = new Tournament();
        t.setId(id);
        t.setName(name);
        t.setStartDate(LocalDateTime.of(2024, 6, 1, 9, 0));
        t.setEndDate(LocalDateTime.of(2024, 8, 31, 18, 0));
        t.setCurrentState(TournamentState.ACTIVE);
        return t;
    }

    // ── findByTournament ─────────────────────────────────────────────────

    @Test
    void findByTournament_returnsStandingsResponse() {
        Tournament tournament = buildTournament("T001", "Torneo Verano");
        Team team = buildTeam("E001", "Los Galacticos");

        Standings s = new Standings();
        s.setId("S001");
        s.setTournamentId("T001");
        s.setTeam(team);
        s.setMatchesPlayed(3);
        s.setMatchesWon(2);
        s.setMatchesDrawn(1);
        s.setMatchesLost(0);
        s.setGoalsFor(5);
        s.setGoalsAgainst(2);
        s.setGoalsDifference(3);
        s.setPoints(7);

        when(tournamentService.findById("T001")).thenReturn(tournament);
        when(standingsService.findByTournamentId("T001")).thenReturn(List.of(s));

        ResponseEntity<?> response = standingsController.findByTournament("T001");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(tournamentService).findById("T001");
        verify(standingsService).findByTournamentId("T001");
    }

    // ── registerTeam ─────────────────────────────────────────────────────

    @Test
    void registerTeam_withExistingTeam_returnsOk() {
        Team team = buildTeam("E001", "Los Galacticos");
        when(teamRepository.findById("E001")).thenReturn(Optional.of(team));

        ResponseEntity<String> response = standingsController.registerTeam("T001", "E001");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Los Galacticos"));
        verify(standingsService).registerTeamInTournament("T001", team);
    }

    @Test
    void registerTeam_withNonExistentTeam_returnsBadRequest() {
        when(teamRepository.findById("E999")).thenReturn(Optional.empty());

        ResponseEntity<String> response = standingsController.registerTeam("T001", "E999");

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("No existe equipo"));
        verify(standingsService, never()).registerTeamInTournament(anyString(), any());
    }
}
