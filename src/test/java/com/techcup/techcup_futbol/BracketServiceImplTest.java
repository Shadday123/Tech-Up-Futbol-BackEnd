package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.BracketException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.BracketServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.*;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import com.techcup.techcup_futbol.persistence.repository.TournamentBracketsRepository;
import com.techcup.techcup_futbol.persistence.repository.TournamentRepository;
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
class BracketServiceImplTest {

    @Mock private TournamentRepository tournamentRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private TournamentBracketsRepository tournamentBracketsRepository;
    @Mock private MatchRepository matchRepository;

    @InjectMocks private BracketServiceImpl bracketService;

    private TournamentEntity tournamentEntity;
    private TeamEntity team1, team2;
    private TournamentBracketsEntity bracketsEntity;

    @BeforeEach
    void setUp() {
        tournamentEntity = new TournamentEntity();
        tournamentEntity.setId("t-001");

        team1 = new TeamEntity(); team1.setId("team-001");
        team2 = new TeamEntity(); team2.setId("team-002");

        bracketsEntity = new TournamentBracketsEntity();
        bracketsEntity.setId("bracket-001");
        bracketsEntity.setTournament(tournamentEntity);
        bracketsEntity.setPhase(PhaseEnum.INITIAL_ROUND);
    }

    @Test
    void generate_validTournamentAndTeams_createsBrackets() {
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findAll()).thenReturn(List.of(team1, team2));
        when(tournamentBracketsRepository.findByTournamentId("t-001")).thenReturn(List.of());

        List<TournamentBracketsEntity> result = bracketService.generate("t-001", 4);

        verify(tournamentBracketsRepository).save(any(TournamentBracketsEntity.class));
        assertFalse(result.isEmpty());
    }

    @Test
    void generate_tournamentNotFound_throwsException() {
        when(tournamentRepository.findById("t-999")).thenReturn(Optional.empty());

        BracketException ex = assertThrows(BracketException.class,
                () -> bracketService.generate("t-999", 4));
        assertEquals("tournamentId", ex.getField());
    }

    @Test
    void generate_notEnoughTeams_throwsException() {
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findAll()).thenReturn(List.of());
        when(tournamentBracketsRepository.findByTournamentId("t-001")).thenReturn(List.of());

        BracketException ex = assertThrows(BracketException.class,
                () -> bracketService.generate("t-001", 4));
        assertEquals("teams", ex.getField());
    }

    @Test
    void generate_notPowerOfTwo_throwsException() {
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findAll()).thenReturn(List.of(team1));
        when(tournamentBracketsRepository.findByTournamentId("t-001")).thenReturn(List.of());

        BracketException ex = assertThrows(BracketException.class,
                () -> bracketService.generate("t-001", 3));
        assertEquals("teams", ex.getField());
    }

    @Test
    void generate_resultsAlreadyRegistered_throwsException() {
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        MatchEntity finishedMatch = new MatchEntity();
        finishedMatch.setStatus(MatchStatus.FINISHED);
        bracketsEntity.setMatches(List.of(finishedMatch));
        when(tournamentBracketsRepository.findByTournamentId("t-001")).thenReturn(List.of(bracketsEntity));

        BracketException ex = assertThrows(BracketException.class,
                () -> bracketService.generate("t-001", 4));
        assertEquals("bracket", ex.getField());
    }

    @Test
    void findByTournamentId_existing_returnsBrackets() {
        when(tournamentBracketsRepository.findByTournamentId("t-001")).thenReturn(List.of(bracketsEntity));

        List<TournamentBrackets> result = bracketService.findByTournamentId("t-001");

        assertEquals(1, result.size());
        assertEquals("t-001", result.get(0).getTournament().getId());
    }

    @Test
    void findByTournamentId_notFound_throwsException() {
        when(tournamentBracketsRepository.findByTournamentId("t-999")).thenReturn(List.of());

        BracketException ex = assertThrows(BracketException.class,
                () -> bracketService.findByTournamentId("t-999"));
        assertEquals("bracket", ex.getField());
    }

    @Test
    void advanceWinner_validMatch_advances() {
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));

        MatchEntity matchWithWinner = new MatchEntity();
        matchWithWinner.setId("match-001");
        matchWithWinner.setScoreLocal(2);
        matchWithWinner.setScoreVisitor(1);
        matchWithWinner.setLocalTeam(team1);
        matchWithWinner.setVisitorTeam(team2);
        matchWithWinner.setStatus(MatchStatus.SCHEDULED);

        when(matchRepository.findById("match-001")).thenReturn(Optional.of(matchWithWinner));
        bracketsEntity.setMatches(List.of(matchWithWinner));
        when(tournamentBracketsRepository.findByTournamentId("t-001")).thenReturn(List.of(bracketsEntity));

        List<TournamentBrackets> result = bracketService.advanceWinner("t-001", "match-001");

        verify(matchRepository).save(matchWithWinner);
        assertFalse(result.isEmpty());
        assertEquals(MatchStatus.FINISHED, matchWithWinner.getStatus());
    }

    @Test
    void advanceWinner_drawMatch_throwsException() {
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        MatchEntity drawMatch = new MatchEntity();
        drawMatch.setScoreLocal(1);
        drawMatch.setScoreVisitor(1);
        when(matchRepository.findById("match-001")).thenReturn(Optional.of(drawMatch));

        BracketException ex = assertThrows(BracketException.class,
                () -> bracketService.advanceWinner("t-001", "match-001"));
        assertEquals("match", ex.getField());
    }

    @Test
    void advanceWinner_matchNotFound_throwsException() {
        when(tournamentRepository.findById("t-001")).thenReturn(Optional.of(tournamentEntity));
        when(matchRepository.findById("match-999")).thenReturn(Optional.empty());

        BracketException ex = assertThrows(BracketException.class,
                () -> bracketService.advanceWinner("t-001", "match-999"));
        assertEquals("matchId", ex.getField());
    }
}