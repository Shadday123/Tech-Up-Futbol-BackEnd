package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.service.TeamServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.mapper.TeamPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamEntity teamEntity;
    private Team team;
    private StudentPlayerEntity captainEntity;
    private StudentPlayerEntity player2Entity;
    private StudentPlayer captain;
    private StudentPlayer player2;

    @BeforeEach
    void setUp() {
        captain = new StudentPlayer();
        captain.setId("cap-001");
        captain.setFullname("Juan Esteban Rodriguez");
        captain.setEmail("juanes@gmail.com");
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
        team.setPlayers(List.of(captain, player2));

        captainEntity = (StudentPlayerEntity) TeamPersistenceMapper.toEntity(team).getCaptain();
        player2Entity = (StudentPlayerEntity) TeamPersistenceMapper.toEntity(team).getPlayers().get(1);
        teamEntity = TeamPersistenceMapper.toEntity(team);
    }

    // ── CREATE TEAM ──

    @Test
    void createTeam_valid_setsIdAndSaves() {
        when(teamRepository.findAll()).thenReturn(List.of());
        when(teamRepository.save(any(TeamEntity.class))).thenAnswer(inv -> {
            TeamEntity entity = inv.getArgument(0);
            entity.setId("team-001");
            return entity;
        });

        Team result = teamService.createTeam(team);

        assertNotNull(result.getId());
        verify(teamRepository).save(any(TeamEntity.class));
    }

    @Test
    void createTeam_teamNameExists_throwsException() {
        List<TeamEntity> existingTeams = new ArrayList<>();
        existingTeams.add(teamEntity);
        when(teamRepository.findAll()).thenReturn(existingTeams);

        TeamException exception = assertThrows(TeamException.class,
                () -> teamService.createTeam(team));

        assertEquals("teamName", exception.getField());
    }

    // ── INVITE PLAYER ──

    @Test
    void invitePlayer_valid_addsPlayer() {
        team.setId("team-001");
        teamEntity.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));
        when(teamRepository.save(teamEntity)).thenReturn(teamEntity);

        StudentPlayer newPlayer = new StudentPlayer();
        newPlayer.setId("p-003");
        newPlayer.setFullname("Nuevo Jugador");
        newPlayer.setAge(20);
        newPlayer.setPosition(PositionEnum.Winger);
        newPlayer.setDisponible(true);
        newPlayer.setHaveTeam(false);
        newPlayer.setSemester(3);

        teamService.invitePlayer("team-001", newPlayer);

        verify(teamRepository).save(teamEntity);
    }

    @Test
    void invitePlayer_notAvailable_throwsException() {
        team.setId("team-001");
        teamEntity.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));

        StudentPlayer unavailablePlayer = new StudentPlayer();
        unavailablePlayer.setId("p-003");
        unavailablePlayer.setFullname("No Disponible");
        unavailablePlayer.setDisponible(false);

        TeamException exception = assertThrows(TeamException.class,
                () -> teamService.invitePlayer("team-001", unavailablePlayer));

        assertEquals("disponibilidad", exception.getField());
    }

    // ── REMOVE PLAYER ──

    @Test
    void removePlayer_valid_removesPlayer() {
        team.setId("team-001");
        teamEntity.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));
        when(teamRepository.save(teamEntity)).thenReturn(teamEntity);

        teamService.removePlayer("team-001", "p-002");

        verify(teamRepository).save(teamEntity);
    }

    @Test
    void removePlayer_captain_throwsException() {
        team.setId("team-001");
        teamEntity.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));

        TeamException exception = assertThrows(TeamException.class,
                () -> teamService.removePlayer("team-001", "cap-001"));

        assertEquals("captain", exception.getField());
    }

    @Test
    void removePlayer_lastPlayer_throwsException() {
        team.setId("team-001");
        Team soloTeam = new Team();
        soloTeam.setId("team-001");
        soloTeam.setTeamName("Solo Team");
        soloTeam.setPlayers(List.of(player2)); // No captain in players list

        TeamEntity soloTeamEntity = TeamPersistenceMapper.toEntity(soloTeam);
        soloTeamEntity.setId("team-001");

        when(teamRepository.findById("team-001")).thenReturn(Optional.of(soloTeamEntity));

        TeamException exception = assertThrows(TeamException.class,
                () -> teamService.removePlayer("team-001", "p-002"));

        assertEquals("players", exception.getField());
    }

    @Test
    void removePlayer_notInTeam_throwsException() {
        team.setId("team-001");
        teamEntity.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));

        TeamException exception = assertThrows(TeamException.class,
                () -> teamService.removePlayer("team-001", "p-nonexistent"));

        assertEquals("player", exception.getField());
    }

    // ── GET ALL TEAMS ──

    @Test
    void getAllTeams_returnsList() {
        List<TeamEntity> teams = List.of(teamEntity);
        when(teamRepository.findAll()).thenReturn(teams);

        List<Team> result = teamService.getAllTeams();

        assertEquals(1, result.size());
        verify(teamRepository).findAll();
    }

    // ── OBTENER POR ID ──

    @Test
    void obtenerPorId_notFound_throwsException() {
        when(teamRepository.findById("team-999")).thenReturn(Optional.empty());

        TeamException exception = assertThrows(TeamException.class,
                () -> teamService.obtenerPorId("team-999"));

        assertEquals("id", exception.getField());
    }

    // ── DELETE TEAM ──

    @Test
    void deleteTeam_existing_unbindsPlayersAndDeletes() {
        team.setId("team-001");
        teamEntity.setId("team-001");
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));

        teamService.deleteTeam("team-001");

        verify(teamRepository).deleteById("team-001");
    }

    // ── NUEVOS MÉTODOS ──

    @Test
    void existsByTeamName_returnsTrue() {
        when(teamRepository.existsByTeamName("Los Tigres")).thenReturn(true);

        boolean result = teamService.existsByTeamName("Los Tigres");

        assertTrue(result);
        verify(teamRepository).existsByTeamName("Los Tigres");
    }

    @Test
    void findByCaptainId_returnsList() {
        List<TeamEntity> captainTeams = List.of(teamEntity);
        when(teamRepository.findByCaptainId("cap-001")).thenReturn(captainTeams);

        List<Team> result = teamService.findByCaptainId("cap-001");

        assertEquals(1, result.size());
        verify(teamRepository).findByCaptainId("cap-001");
    }
}