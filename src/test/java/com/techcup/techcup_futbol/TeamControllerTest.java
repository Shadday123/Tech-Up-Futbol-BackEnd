package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.TeamController;
import com.techcup.techcup_futbol.controller.dto.CreateTeamRequest;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.service.PlayerService;
import com.techcup.techcup_futbol.core.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock TeamService teamService;
    @Mock PlayerService playerService;
    @InjectMocks TeamController teamController;

    private Team team;
    private Player captain;

    @BeforeEach
    void setUp() {
        captain = new StudentPlayer();
        captain.setId("p1");
        captain.setFullname("Capitan");

        team = new Team();
        team.setId("t1");
        team.setTeamName("Los Mejores");
        team.setShieldUrl("shield.png");
        team.setUniformColors("Rojo");
        team.setCaptain(captain);
        team.setPlayers(new ArrayList<>(List.of(captain)));
    }

    @Test
    void create_validRequest_returnsCreated() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setTeamName("Los Mejores");
        request.setShieldUrl("shield.png");
        request.setUniformColors("Rojo");
        request.setCaptainId("p1");
        request.setPlayerIds(List.of("p1"));

        when(playerService.obtenerPorId("p1")).thenReturn(captain);
        when(teamService.createTeam(any(Team.class))).thenReturn(team);

        ResponseEntity<?> response = teamController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(teamService).createTeam(any(Team.class));
    }

    @Test
    void findAll_returnsOkWithList() {
        when(teamService.getAllTeams()).thenReturn(List.of(team));

        ResponseEntity<?> response = teamController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamService).getAllTeams();
    }

    @Test
    void findById_existing_returnsOk() {
        when(teamService.buscarPorId("t1")).thenReturn(Optional.of(team));

        ResponseEntity<?> response = teamController.findById("t1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void findById_notFound_returnsNotFound() {
        when(teamService.buscarPorId("t999")).thenReturn(Optional.empty());

        ResponseEntity<?> response = teamController.findById("t999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void delete_existing_returnsNoContent() {
        doNothing().when(teamService).deleteTeam("t1");

        ResponseEntity<Void> response = teamController.delete("t1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(teamService).deleteTeam("t1");
    }

    @Test
    void invitePlayer_returnsOk() {
        when(playerService.obtenerPorId("p1")).thenReturn(captain);
        doNothing().when(teamService).invitePlayer("t1", captain);

        ResponseEntity<Void> response = teamController.invitePlayer("t1", "p1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamService).invitePlayer("t1", captain);
    }

    @Test
    void removePlayer_returnsNoContent() {
        doNothing().when(teamService).removePlayer("t1", "p1");

        ResponseEntity<Void> response = teamController.removePlayer("t1", "p1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(teamService).removePlayer("t1", "p1");
    }
}
