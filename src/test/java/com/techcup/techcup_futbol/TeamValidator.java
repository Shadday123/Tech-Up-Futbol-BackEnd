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

    @Test
    void validatePlayerAddition_playerAlreadyHasTeam_throwsException() {
        Player player = mock(Player.class);
        when(player.isHaveTeam()).thenReturn(true);
        when(player.getFullname()).thenReturn("Jugador Ocupado");

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validatePlayerAddition(validTeam, player));
        assertEquals("player", ex.getField());
    }

    @Test
    void validatePlayerAddition_teamFull_throwsException() {
        Player player = mock(Player.class);
        when(player.isHaveTeam()).thenReturn(false);

        List<Player> fullPlayers = new java.util.ArrayList<>();
        for (int i = 0; i < 12; i++) fullPlayers.add(mock(Player.class));
        when(validTeam.getPlayers()).thenReturn(fullPlayers);

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validatePlayerAddition(validTeam, player));
        assertEquals("players", ex.getField());
    }

    @Test
    void validateCreationPlayers_playerWithTeam_throwsException() {
        Player captain = mock(Player.class);
        when(captain.getId()).thenReturn("CAP001");
        Player playerWithTeam = mock(Player.class);
        when(playerWithTeam.getId()).thenReturn("CAP001");
        when(playerWithTeam.isHaveTeam()).thenReturn(true);
        when(playerWithTeam.getFullname()).thenReturn("Ocupado");

        when(validTeam.getCaptain()).thenReturn(captain);
        when(validTeam.getPlayers()).thenReturn(List.of(playerWithTeam));

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateCreationPlayers(validTeam));
        assertEquals("player", ex.getField());
    }

    @Test
    void validateCreationPlayers_playerNotAvailable_throwsException() {
        Player captain = mock(Player.class);
        when(captain.getId()).thenReturn("CAP001");
        Player unavailable = mock(Player.class);
        when(unavailable.getId()).thenReturn("CAP001");
        when(unavailable.isHaveTeam()).thenReturn(false);
        when(unavailable.isDisponible()).thenReturn(false);
        when(unavailable.getFullname()).thenReturn("Indisponible");

        when(validTeam.getCaptain()).thenReturn(captain);
        when(validTeam.getPlayers()).thenReturn(List.of(unavailable));

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validateCreationPlayers(validTeam));
        assertEquals("disponibilidad", ex.getField());
    }

    @Test
    void validate_tooFewPlayers_throwsException() {
        when(validTeam.getPlayers()).thenReturn(List.of(
                mock(StudentPlayer.class),
                mock(Player.class)
        ));

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validate(validTeam, otherTeams));
        assertEquals("players", ex.getField());
    }

    @Test
    void validate_tooManyPlayers_throwsException() {
        List<Player> tooMany = new java.util.ArrayList<>();
        for (int i = 0; i < 13; i++) tooMany.add(mock(StudentPlayer.class));
        when(validTeam.getPlayers()).thenReturn(tooMany);

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validate(validTeam, otherTeams));
        assertEquals("players", ex.getField());
    }

    @Test
    void validate_nullPlayersList_throwsException() {
        when(validTeam.getPlayers()).thenReturn(null);

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validate(validTeam, otherTeams));
        assertEquals("players", ex.getField());
    }

    @Test
    void validate_notEnoughStudents_throwsException() {
        when(validTeam.getPlayers()).thenReturn(List.of(
                mock(Player.class),
                mock(Player.class),
                mock(Player.class),
                mock(Player.class),
                mock(Player.class),
                mock(Player.class),
                mock(Player.class)
        ));

        TeamException ex = assertThrows(TeamException.class,
                () -> TeamValidator.validate(validTeam, otherTeams));
        assertEquals("players", ex.getField());
    }

    @Test
    void validateDuplicatePlayers_playerInMultipleTeams_throwsException() {
        Player sharedPlayer = mock(Player.class);
        when(sharedPlayer.getId()).thenReturn("SHARED001");
        when(sharedPlayer.getFullname()).thenReturn("Shared Player");

        Team team1 = mock(Team.class);
        when(team1.getTeamName()).thenReturn("Team A");
        when(team1.getPlayers()).thenReturn(List.of(sharedPlayer));

        Team team2 = mock(Team.class);
        when(team2.getTeamName()).thenReturn("Team B");
        when(team2.getPlayers()).thenReturn(List.of(sharedPlayer));

        // Use a team with the shared player that already appears in 2 teams
        List<StudentPlayer> validCount = new java.util.ArrayList<>();
        for (int i = 0; i < 8; i++) {
            StudentPlayer sp = mock(StudentPlayer.class);
            when(sp.getId()).thenReturn("SHARED001");
            when(sp.getFullname()).thenReturn("Shared Player");
            validCount.add(sp);
        }
        when(validTeam.getPlayers()).thenReturn(new java.util.ArrayList<>(validCount));
        when(validTeam.getTeamName()).thenReturn("Team C");

        // Two other teams have the same player
        Team otherA = mock(Team.class);
        when(otherA.getTeamName()).thenReturn("Team A");
        StudentPlayer dupeA = mock(StudentPlayer.class);
        when(dupeA.getId()).thenReturn("SHARED001");
        when(otherA.getPlayers()).thenReturn(List.of(dupeA));

        Team otherB = mock(Team.class);
        when(otherB.getTeamName()).thenReturn("Team B");
        StudentPlayer dupeB = mock(StudentPlayer.class);
        when(dupeB.getId()).thenReturn("SHARED001");
        when(otherB.getPlayers()).thenReturn(List.of(dupeB));

        assertThrows(TeamException.class,
                () -> TeamValidator.validate(validTeam, List.of(otherA, otherB)));
    }

    @Test
    void validateTeamName_nullOtherTeams_doesNotThrow() {
        assertDoesNotThrow(() -> TeamValidator.validateTeamName("Nuevo Equipo", null));
    }

    @Test
    void validateCaptain_withValidCaptain_doesNotThrow() {
        Player captain = mock(Player.class);
        when(validTeam.getCaptain()).thenReturn(captain);
        assertDoesNotThrow(() -> TeamValidator.validateCaptain(validTeam));
    }
}