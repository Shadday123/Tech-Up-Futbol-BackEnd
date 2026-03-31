package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.service.TeamServiceImpl;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team team;
    private StudentPlayer captain;
    private StudentPlayer player2;

    @BeforeEach
    void setUp() {
        captain = new StudentPlayer();
        captain.setId("cap-001");
        captain.setFullname("Capitan Rodriguez");
        captain.setEmail("capitan@gmail.com");
        captain.setAge(23);
        captain.setPosition(PositionEnum.Midfielder);
        captain.setCaptain(true);
        captain.setDisponible(true);
        captain.setHaveTeam(false);
        captain.setSemester(6);

        player2 = new StudentPlayer();
        player2.setId("p-002");
        player2.setFullname("Jugador Dos");
        player2.setEmail("jugador2@gmail.com");
        player2.setAge(21);
        player2.setPosition(PositionEnum.Defender);
        player2.setDisponible(true);
        player2.setHaveTeam(false);
        player2.setSemester(4);

        team = new Team();
        team.setTeamName("Los Tigres");
        team.setCaptain(captain);
        team.setPlayers(new ArrayList<>(List.of(captain, player2)));
    }

    // ── CREATE TEAM ──

    @Test
    void createTeam_valid_setsIdAndSaves() {
        when(teamRepository.findAll()).thenReturn(List.of());
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        Team result = teamService.createTeam(team);

        assertNotNull(result.getId());
        assertTrue(captain.isHaveTeam());
        assertTrue(player2.isHaveTeam());
        verify(teamRepository).save(team);
    }

    // ── INVITE PLAYER ──

    @Test
    void invitePlayer_valid_addsPlayer() {
        team.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        StudentPlayer newPlayer = new StudentPlayer();
        newPlayer.setId("p-003");
        newPlayer.setFullname("Nuevo Jugador");
        newPlayer.setAge(20);
        newPlayer.setPosition(PositionEnum.Winger);
        newPlayer.setDisponible(true);
        newPlayer.setHaveTeam(false);
        newPlayer.setSemester(3);

        int sizeBefore = team.getPlayers().size();

        teamService.invitePlayer("team-001", newPlayer);

        assertEquals(sizeBefore + 1, team.getPlayers().size());
        assertTrue(newPlayer.isHaveTeam());
    }

    @Test
    void invitePlayer_notAvailable_throwsException() {
        team.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        StudentPlayer unavailablePlayer = new StudentPlayer();
        unavailablePlayer.setId("p-003");
        unavailablePlayer.setFullname("No Disponible");
        unavailablePlayer.setAge(20);
        unavailablePlayer.setPosition(PositionEnum.Winger);
        unavailablePlayer.setDisponible(false);
        unavailablePlayer.setHaveTeam(false);
        unavailablePlayer.setSemester(3);

        assertThrows(TeamException.class,
                () -> teamService.invitePlayer("team-001", unavailablePlayer));
    }

    // ── REMOVE PLAYER ──

    @Test
    void removePlayer_valid_removesPlayer() {
        team.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        teamService.removePlayer("team-001", "p-002");

        assertFalse(team.getPlayers().contains(player2));
        assertFalse(player2.isHaveTeam());
    }

    @Test
    void removePlayer_captain_throwsException() {
        team.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        assertThrows(TeamException.class,
                () -> teamService.removePlayer("team-001", "cap-001"));
    }

    @Test
    void removePlayer_lastPlayer_throwsException() {
        team.setId("team-001");
        // Only one player (the captain) in the team
        team.setPlayers(new ArrayList<>(List.of(captain)));
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        // Attempting to remove the captain (the only player) should fail for captain reason first,
        // but with a non-captain single player it would fail for "last player" reason.
        // Let's use a non-captain single player scenario.
        StudentPlayer soloPlayer = new StudentPlayer();
        soloPlayer.setId("solo-001");
        soloPlayer.setFullname("Solo Player");
        soloPlayer.setAge(20);
        soloPlayer.setPosition(PositionEnum.GoalKeeper);
        soloPlayer.setSemester(3);

        team.setCaptain(captain); // captain not in the list
        team.setPlayers(new ArrayList<>(List.of(soloPlayer)));

        assertThrows(TeamException.class,
                () -> teamService.removePlayer("team-001", "solo-001"));
    }

    @Test
    void removePlayer_notInTeam_throwsException() {
        team.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        assertThrows(TeamException.class,
                () -> teamService.removePlayer("team-001", "p-nonexistent"));
    }

    // ── GET ALL TEAMS ──

    @Test
    void getAllTeams_returnsList() {
        when(teamRepository.findAll()).thenReturn(List.of(team));

        List<Team> result = teamService.getAllTeams();

        assertEquals(1, result.size());
        verify(teamRepository).findAll();
    }

    // ── OBTENER POR ID ──

    @Test
    void obtenerPorId_notFound_throwsException() {
        when(teamRepository.findById("team-999")).thenReturn(Optional.empty());

        assertThrows(TeamException.class,
                () -> teamService.obtenerPorId("team-999"));
    }

    // ── DELETE TEAM ──

    @Test
    void deleteTeam_existing_unbindsPlayersAndDeletes() {
        team.setId("team-001");
        captain.setHaveTeam(true);
        player2.setHaveTeam(true);
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        teamService.deleteTeam("team-001");

        assertFalse(captain.isHaveTeam());
        assertFalse(player2.isHaveTeam());
        verify(teamRepository).deleteById("team-001");
    }
}
