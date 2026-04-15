package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.LineupException;
import com.techcup.techcup_futbol.core.model.Lineup;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.service.LineupServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.*;
import com.techcup.techcup_futbol.persistence.repository.LineupRepository;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LineupServiceImplTest {

    @Mock private LineupRepository lineupRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private PlayerRepository playerRepository;

    @InjectMocks private LineupServiceImpl lineupService;

    private MatchEntity matchEntity;
    private TeamEntity teamEntity;
    private PlayerEntity player1, player2;

    @BeforeEach
    void setUp() {
        matchEntity = new MatchEntity();
        matchEntity.setId("match-001");

        teamEntity = new TeamEntity();
        teamEntity.setId("team-001");
        teamEntity.setTeamName("Los cantores");

        player1 = new StudentPlayerEntity();
        player1.setId("p-001");

        player2 = new RelativePlayerEntity();
        player2.setId("p-002");

    }

    @Test
    void create_validParams_createsLineup() {
        when(matchRepository.findById("match-001")).thenReturn(Optional.of(matchEntity));
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));

        for (int i = 1; i <= 7; i++) {
            PlayerEntity player = new StudentPlayerEntity();
            player.setId("p-00" + i);
            when(playerRepository.findById("p-00" + i)).thenReturn(Optional.of(player));
        }

        when(lineupRepository.existsByMatchIdAndTeamId("match-001", "team-001")).thenReturn(false);

        LineUpEntity savedEntity = new LineUpEntity();
        savedEntity.setId("lineup-001");
        when(lineupRepository.save(any(LineUpEntity.class))).thenReturn(savedEntity);

        List<String> starters = List.of("p-001", "p-002", "p-003", "p-004", "p-005", "p-006", "p-007");

        Lineup result = lineupService.create("match-001", "team-001", "4-3-3",
                starters, List.of(), List.of("GK:1, FW:2"));

        assertEquals("lineup-001", result.getId());
        verify(lineupRepository).save(any(LineUpEntity.class));
    }

    @Test
    void create_matchNotFound_throwsException() {
        when(matchRepository.findById("match-999")).thenReturn(Optional.empty());

        LineupException ex = assertThrows(LineupException.class,
                () -> lineupService.create("match-999", "team-001", "4-3-3", List.of(), List.of(), List.of()));
        assertEquals("matchId", ex.getField());
    }

    @Test
    void create_teamNotFound_throwsException() {
        when(matchRepository.findById("match-001")).thenReturn(Optional.of(matchEntity));
        when(teamRepository.findById("team-999")).thenReturn(Optional.empty());

        LineupException ex = assertThrows(LineupException.class,
                () -> lineupService.create("match-001", "team-999", "4-3-3", List.of(), List.of(), List.of()));
        assertEquals("teamId", ex.getField());
    }

    @Test
    void create_lineupAlreadyExists_throwsException() {
        when(matchRepository.findById("match-001")).thenReturn(Optional.of(matchEntity));
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));
        when(lineupRepository.existsByMatchIdAndTeamId("match-001", "team-001")).thenReturn(true);

        LineupException ex = assertThrows(LineupException.class,
                () -> lineupService.create("match-001", "team-001", "4-3-3", List.of(), List.of(), List.of()));
        assertEquals("lineup", ex.getField());
    }

    @Test
    void create_wrongStartersCount_throwsException() {
        when(matchRepository.findById("match-001")).thenReturn(Optional.of(matchEntity));
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));
        when(lineupRepository.existsByMatchIdAndTeamId("match-001", "team-001")).thenReturn(false);

        LineupException ex = assertThrows(LineupException.class,
                () -> lineupService.create("match-001", "team-001", "4-3-3",
                        List.of("p-001"), List.of(), List.of()));
        assertEquals("starters", ex.getField());
    }

    @Test
    void findByMatchAndTeam_existing_returnsLineup() {
        LineUpEntity entity = new LineUpEntity();
        entity.setId("lineup-001");
        when(lineupRepository.findByMatchIdAndTeamId("match-001", "team-001"))
                .thenReturn(Optional.of(entity));

        Lineup result = lineupService.findByMatchAndTeam("match-001", "team-001");

        assertEquals("lineup-001", result.getId());
    }

    @Test
    void findByMatchAndTeam_notFound_throwsException() {
        when(lineupRepository.findByMatchIdAndTeamId("match-001", "team-001")).thenReturn(Optional.empty());

        LineupException ex = assertThrows(LineupException.class,
                () -> lineupService.findByMatchAndTeam("match-001", "team-001"));
        assertEquals("lineup", ex.getField());
    }

    @Test
    void findRivalLineup_valid_returnsRival() {
        MatchEntity match = new MatchEntity();
        match.setId("match-001");
        match.setLocalTeam(teamEntity);
        TeamEntity rivalTeam = new TeamEntity();
        rivalTeam.setId("team-002");
        match.setVisitorTeam(rivalTeam);

        LineUpEntity rivalLineup = new LineUpEntity();
        rivalLineup.setId("rival-lineup-001");

        when(matchRepository.findById("match-001")).thenReturn(Optional.of(match));
        when(lineupRepository.findByMatchIdAndTeamId("match-001", "team-002"))
                .thenReturn(Optional.of(rivalLineup));

        Lineup result = lineupService.findRivalLineup("match-001", "team-001");

        assertEquals("rival-lineup-001", result.getId());
    }

    @Test
    void findRivalLineup_rivalNotPublished_throwsException() {
        MatchEntity match = new MatchEntity();
        match.setId("match-001");
        match.setLocalTeam(teamEntity);
        TeamEntity rivalTeam = new TeamEntity();
        rivalTeam.setId("team-002");
        match.setVisitorTeam(rivalTeam);

        when(matchRepository.findById("match-001")).thenReturn(Optional.of(match));
        when(lineupRepository.findByMatchIdAndTeamId("match-001", "team-002")).thenReturn(Optional.empty());

        LineupException ex = assertThrows(LineupException.class,
                () -> lineupService.findRivalLineup("match-001", "team-001"));
        assertEquals("lineup", ex.getField());
    }

    @Test
    void findRivalLineup_matchNotFound_throwsException() {
        when(matchRepository.findById("match-999")).thenReturn(Optional.empty());

        LineupException ex = assertThrows(LineupException.class,
                () -> lineupService.findRivalLineup("match-999", "team-001"));
        assertEquals("matchId", ex.getField());
    }

    @Test
    void registerMatch_valid_logsLineups() {
        LineUpEntity lineup1 = new LineUpEntity();
        LineUpEntity lineup2 = new LineUpEntity();
        when(lineupRepository.findByMatchId("match-001")).thenReturn(List.of(lineup1, lineup2));

        Match match = new Match();
        match.setId("match-001");

        lineupService.registerMatch(match);

        verify(lineupRepository).findByMatchId("match-001");
    }
}