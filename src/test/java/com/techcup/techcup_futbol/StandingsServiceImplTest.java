package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.service.StandingsServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.StandingsEntity;
import com.techcup.techcup_futbol.persistence.entity.TournamentEntity;
import com.techcup.techcup_futbol.persistence.repository.StandingsRepository;
import com.techcup.techcup_futbol.persistence.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandingsServiceImplTest {

    @Mock private StandingsRepository standingsRepository;
    @Mock private TournamentRepository tournamentRepository;

    @InjectMocks private StandingsServiceImpl standingsService;


    @Test
    void registerTeamInTournament_notFound_throws() {
        when(tournamentRepository.findById("t999")).thenReturn(Optional.empty());

        assertThrows(TournamentException.class,
                () -> standingsService.registerTeamInTournament("t999", mock(Team.class)));
    }

    @Test
    void updateFromMatch_hasData_updates() {
        Match match = mock(Match.class);
        when(match.getScoreLocal()).thenReturn(1);
        when(standingsRepository.findByTournamentId(anyString())).thenReturn(List.of(new StandingsEntity()));
        when(standingsRepository.save(any())).thenReturn(new StandingsEntity());

        standingsService.updateFromMatch(match);

        verify(standingsRepository).save(any());
    }

    @Test
    void updateFromMatch_noData_skips() {
        Match match = mock(Match.class);
        when(standingsRepository.findByTournamentId(anyString())).thenReturn(List.of());

        standingsService.updateFromMatch(match);

        verify(standingsRepository, never()).save(any());
    }

    @Test
    void findByTournamentId_valid_returnsData() {
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of((TournamentEntity) mock(TournamentEntity.class)));
        when(standingsRepository.findByTournamentId("t1")).thenReturn(List.of(new StandingsEntity()));

        var result = standingsService.findByTournamentId("t1");

        assertFalse(result.isEmpty());
    }

    @Test
    void findByTournamentId_notFound_throws() {
        when(tournamentRepository.findById("t999")).thenReturn(Optional.empty());

        assertThrows(TournamentException.class,
                () -> standingsService.findByTournamentId("t999"));
    }

    @Test
    void findByTournamentId_empty_returnsEmpty() {
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of((TournamentEntity) mock(TournamentEntity.class)));
        when(standingsRepository.findByTournamentId("t1")).thenReturn(List.of());

        var result = standingsService.findByTournamentId("t1");

        assertTrue(result.isEmpty());
    }

}