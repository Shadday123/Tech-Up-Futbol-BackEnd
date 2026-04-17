package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.MatchException;
import com.techcup.techcup_futbol.core.model.MatchStatus;
import com.techcup.techcup_futbol.core.service.MatchServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.repository.MatchEventRepository;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
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
class MatchServiceImplTest {

    @Mock private MatchRepository matchRepository;
    @Mock private MatchEventRepository matchEventRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private PlayerRepository playerRepository;

    @InjectMocks private MatchServiceImpl matchService;

    private TeamEntity localTeamEntity, visitorTeamEntity;
    private MatchEntity matchEntity;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        localTeamEntity = new TeamEntity();
        localTeamEntity.setId("team1");
        localTeamEntity.setTeamName("Dragons");

        visitorTeamEntity = new TeamEntity();
        visitorTeamEntity.setId("team2");
        visitorTeamEntity.setTeamName("Hawks");

        matchEntity = new MatchEntity();
        matchEntity.setId("match1");
        matchEntity.setLocalTeam(localTeamEntity);
        matchEntity.setVisitorTeam(visitorTeamEntity);
        matchEntity.setDateTime(testDateTime);
        matchEntity.setField(1);
        matchEntity.setStatus(MatchStatus.SCHEDULED);
    }

    @Test
    void create_validTeams_createsMatch() {
        when(teamRepository.findById("team1")).thenReturn(Optional.of(localTeamEntity));
        when(teamRepository.findById("team2")).thenReturn(Optional.of(visitorTeamEntity));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);

        var result = matchService.create("team1", "team2", testDateTime, "ref1", 1);

        verify(matchRepository).save(any(MatchEntity.class));
        assertEquals("match1", result.getId());
    }

    @Test
    void create_localTeamNotFound_throwsException() {
        when(teamRepository.findById("team1")).thenReturn(Optional.empty());

        MatchException ex = assertThrows(MatchException.class,
                () -> matchService.create("team1", "team2", testDateTime, "ref1", 1));
        assertEquals("localTeamId", ex.getField());
    }

    @Test
    void create_sameTeams_throwsException() {
        when(teamRepository.findById("team1")).thenReturn(Optional.of(localTeamEntity));

        MatchException ex = assertThrows(MatchException.class,
                () -> matchService.create("team1", "team1", testDateTime, "ref1", 1));
        assertEquals("teams", ex.getField());
    }

    @Test
    void registerResult_matchNotFound_throwsException() {
        when(matchRepository.findById("match999")).thenReturn(Optional.empty());

        MatchException ex = assertThrows(MatchException.class,
                () -> matchService.registerResult("match999", 1, 0, null));
        assertEquals("matchId", ex.getField());
    }

    @Test
    void registerResult_alreadyFinished_throwsException() {
        matchEntity.setStatus(MatchStatus.FINISHED);
        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));

        MatchException ex = assertThrows(MatchException.class,
                () -> matchService.registerResult("match1", 1, 0, null));
        assertEquals("status", ex.getField());
    }

    @Test
    void findById_existing_returnsMatch() {
        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));

        var result = matchService.findById("match1");

        assertEquals("match1", result.getId());
    }

    @Test
    void findById_notFound_throwsException() {
        when(matchRepository.findById("match999")).thenReturn(Optional.empty());

        MatchException ex = assertThrows(MatchException.class,
                () -> matchService.findById("match999"));
        assertEquals("matchId", ex.getField());
    }

    @Test
    void isResultRegistered_finished_returnsTrue() {
        matchEntity.setStatus(MatchStatus.FINISHED);
        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));

        boolean result = matchService.isResultRegistered("match1");

        assertTrue(result);
    }

    @Test
    void getMatches_returnsMatchesMap() {
        when(matchRepository.findAll()).thenReturn(List.of(matchEntity));

        var result = matchService.getMatches();

        assertEquals(1, result.size());
    }

    @Test
    void create_visitorTeamNotFound_throwsException() {
        when(teamRepository.findById("team1")).thenReturn(Optional.of(localTeamEntity));
        when(teamRepository.findById("team2")).thenReturn(Optional.empty());

        MatchException ex = assertThrows(MatchException.class,
                () -> matchService.create("team1", "team2", testDateTime, "ref1", 1));
        assertEquals("visitorTeamId", ex.getField());
    }

    @Test
    void registerResult_withNullEvents_registersResult() {
        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);

        var result = matchService.registerResult("match1", 0, 0, null);

        assertNotNull(result);
        verify(matchRepository).save(any(MatchEntity.class));
    }

    @Test
    void registerResult_withValidEvents_registersResult() {
        var localPlayer = new com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity();
        localPlayer.setId("p1");
        localTeamEntity.setPlayers(List.of(localPlayer));

        var visitorPlayer = new com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity();
        visitorPlayer.setId("p2");
        visitorTeamEntity.setPlayers(List.of(visitorPlayer));

        matchEntity.setLocalTeam(localTeamEntity);
        matchEntity.setVisitorTeam(visitorTeamEntity);

        var goal = new com.techcup.techcup_futbol.core.model.MatchEventInput("GOAL", 30, "p1");
        var yellow = new com.techcup.techcup_futbol.core.model.MatchEventInput("YELLOW_CARD", 45, "p1");
        var red = new com.techcup.techcup_futbol.core.model.MatchEventInput("RED_CARD", 60, "p2");

        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));
        when(playerRepository.findById("p1")).thenReturn(Optional.of(localPlayer));
        when(playerRepository.findById("p2")).thenReturn(Optional.of(visitorPlayer));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);
        doNothing().when(matchEventRepository).deleteByMatchId("match1");

        var result = matchService.registerResult("match1", 1, 0, List.of(goal, yellow, red));
        assertNotNull(result);
    }

    @Test
    void registerResult_goalsMismatch_throwsException() {
        var player = new com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity();
        player.setId("p1");
        localTeamEntity.setPlayers(List.of(player));
        visitorTeamEntity.setPlayers(List.of());

        var goal = new com.techcup.techcup_futbol.core.model.MatchEventInput("GOAL", 30, "p1");

        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));

        MatchException ex = assertThrows(MatchException.class,
                () -> matchService.registerResult("match1", 2, 0, List.of(goal)));
        assertEquals("events", ex.getField());
    }

    @Test
    void findAll_returnsList() {
        when(matchRepository.findAll()).thenReturn(List.of(matchEntity));

        var result = matchService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByTeamId_returnsMatches() {
        when(matchRepository.findByLocalTeamIdOrVisitorTeamId("team1", "team1"))
                .thenReturn(List.of(matchEntity));

        var result = matchService.findByTeamId("team1");

        assertEquals(1, result.size());
    }

    @Test
    void isResultRegistered_notFound_returnsFalse() {
        when(matchRepository.findById("match999")).thenReturn(Optional.empty());

        boolean result = matchService.isResultRegistered("match999");

        assertFalse(result);
    }

    @Test
    void registerMatch_notExists_saves() {
        when(matchRepository.existsById("match1")).thenReturn(false);

        matchService.registerMatch(matchEntity);

        verify(matchRepository).save(matchEntity);
    }

    @Test
    void registerMatch_alreadyExists_doesNotSave() {
        when(matchRepository.existsById("match1")).thenReturn(true);

        matchService.registerMatch(matchEntity);

        verify(matchRepository, never()).save(any());
    }
}