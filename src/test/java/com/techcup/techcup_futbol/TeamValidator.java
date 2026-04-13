package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.validator.TeamValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamValidatorTest {

    private Team validTeam;
    private List<Team> otherTeams;

    @BeforeEach
    void setUp() {
        validTeam = mock(Team.class);
        when(validTeam.getTeamName()).thenReturn("Equipo Valido");
        when(validTeam.getPlayers()).thenReturn(List.of(
                mock(StudentPlayer.class),
                mock(StudentPlayer.class),
                mock(StudentPlayer.class),
                mock(StudentPlayer.class),
                mock(StudentPlayer.class),
                mock(Player.class),
                mock(Player.class),
                mock(Player.class)
        ));
        when(validTeam.getCaptain()).thenReturn(mock(Player.class));

        otherTeams = List.of();
    }

    @Test
    void validate_withValidTeam_doesNotThrow() {
        assertDoesNotThrow(() -> TeamValidator.validate(validTeam, otherTeams));
    }

    @Test
    void validateTeamName_withValidName_doesNotThrow() {
        assertDoesNotThrow(() -> TeamValidator.validateTeamName("Equipo Nuevo", otherTeams));
    }

    @Test
    void validate_withNullTeam_throwsException() {
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validate(null, otherTeams));
        assertFalse(ex.getMessage().contains("null"));
    }

    @Test
    void validateTeamName_withNullName_throwsException() {
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateTeamName(null, otherTeams));
        assertEquals("teamName", ex.getField());
    }

    @Test
    void validateTeamName_withBlankName_throwsException() {
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateTeamName("   ", otherTeams));
        assertEquals("teamName", ex.getField());
    }

    @Test
    void validateTeamName_withDuplicateName_throwsException() {
        otherTeams = List.of(validTeam);
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateTeamName("Equipo Valido", otherTeams));
        assertEquals("teamName", ex.getField());
    }

    @Test
    void validateCaptain_withNullCaptain_throwsException() {
        when(validTeam.getCaptain()).thenReturn(null);
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateCaptain(validTeam));
        assertEquals("captain", ex.getField());
    }

    @Test
    void validateCreationPlayers_withEmptyPlayers_throwsException() {
        when(validTeam.getPlayers()).thenReturn(List.of());
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateCreationPlayers(validTeam));
        assertEquals("players", ex.getField());
    }

    @Test
    void validateCreationPlayers_captainNotInPlayers_throwsException() {
        Player captain = mock(Player.class);
        when(captain.getId()).thenReturn("CAP001");
        when(validTeam.getCaptain()).thenReturn(captain);
        when(validTeam.getPlayers()).thenReturn(List.of(mock(Player.class)));
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateCreationPlayers(validTeam));
        assertEquals("captain", ex.getField());
    }

    @Test
    void validatePlayerAddition_withNullPlayer_throwsException() {
        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validatePlayerAddition(validTeam, null));
        assertEquals("player", ex.getField());
    }
}