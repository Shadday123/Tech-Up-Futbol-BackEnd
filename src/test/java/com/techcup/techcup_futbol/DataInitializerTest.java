package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import com.techcup.techcup_futbol.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_savesAllPlayers() throws Exception {
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));
        when(playerRepository.findById(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            StudentPlayer p = new StudentPlayer();
            p.setId(id);
            p.setFullname("Player " + id);
            return Optional.of(p);
        });
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        // 9 players: J001-J008 + J-ORG
        verify(playerRepository, times(9)).save(any(Player.class));
    }

    @Test
    void run_savesAllTeams() throws Exception {
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));
        when(playerRepository.findById(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            StudentPlayer p = new StudentPlayer();
            p.setId(id);
            p.setFullname("Player " + id);
            return Optional.of(p);
        });
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        // 3 teams: E001, E002, E003
        verify(teamRepository, times(3)).save(any(Team.class));
    }

    @Test
    void run_savesAllTournaments() throws Exception {
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));
        when(playerRepository.findById(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            StudentPlayer p = new StudentPlayer();
            p.setId(id);
            p.setFullname("Player " + id);
            return Optional.of(p);
        });
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        // 3 tournaments: T001, T002, T003
        verify(tournamentRepository, times(3)).save(any(Tournament.class));
    }

    @Test
    void run_savesOrganizadorWithCorrectRole() throws Exception {
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));
        when(playerRepository.findById(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            StudentPlayer p = new StudentPlayer();
            p.setId(id);
            p.setFullname("Player " + id);
            return Optional.of(p);
        });
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository, times(9)).save(captor.capture());

        Player organizador = captor.getAllValues().stream()
                .filter(p -> "J-ORG".equals(p.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals("organizador@escuelaing.edu.co", organizador.getEmail());
        assertEquals(com.techcup.techcup_futbol.core.model.SystemRole.ORGANIZADOR, organizador.getSystemRole());
        assertNotNull(organizador.getPasswordHash());
    }

    @Test
    void run_teamsLookUpPlayersFromRepository() throws Exception {
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));
        when(playerRepository.findById(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            StudentPlayer p = new StudentPlayer();
            p.setId(id);
            p.setFullname("Player " + id);
            return Optional.of(p);
        });
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        // Teams reference players J001-J008, so findById is called for each
        verify(playerRepository, atLeast(8)).findById(anyString());
    }
}
