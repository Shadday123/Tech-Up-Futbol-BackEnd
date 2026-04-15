package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.StandingsController;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.StandingsService;
import com.techcup.techcup_futbol.core.service.TournamentService;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

    private TeamEntity buildTeamEntity(String id, String name) {
        TeamEntity team = new TeamEntity();
        team.setId(id);
        team.setTeamName(name);
        team.setShieldUrl("shield.png");
        return team;
    }

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

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(tournamentService).findById("T001");
        verify(standingsService).findByTournamentId("T001");
    }

    // ── registerTeam ─────────────────────────────────────────────────────

    @Test
    void registerTeam_withExistingTeam_returnsOk() {
        TeamEntity teamEntity = buildTeamEntity("E001", "Los Galacticos");
        Team team = buildTeam("E001", "Los Galacticos");
        when(teamRepository.findById("E001")).thenReturn(Optional.of(teamEntity));

        ResponseEntity<String> response = standingsController.registerTeam("T001", "E001");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Los Galacticos"));
        verify(standingsService).registerTeamInTournament(eq("T001"), any(Team.class));
    }

    @Test
    void registerTeam_withNonExistentTeam_returnsBadRequest() {
        when(teamRepository.findById("E999")).thenReturn(Optional.empty());

        ResponseEntity<String> response = standingsController.registerTeam("T001", "E999");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("No existe equipo"));
        verify(standingsService, never()).registerTeamInTournament(anyString(), any());
        verify(teamRepository).findById("E999");
    }

    // ── ADICIONALES ──

    @Test
    void findByTournament_tournamentNotFound_returnsNotFound() {
        when(tournamentService.findById("T999")).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<?> response = standingsController.findByTournament("T999");

        assertEquals(404, response.getStatusCode().value());
        verify(tournamentService).findById("T999");
        verify(standingsService, never()).findByTournamentId(anyString());
    }
}
