package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionConstructorTest {

    @Test
    void bracketException_singleArg_hasNullField() {
        BracketException ex = new BracketException("mensaje");
        assertNull(ex.getField());
        assertEquals("mensaje", ex.getMessage());
    }

    @Test
    void lineupException_singleArg_hasNullField() {
        LineupException ex = new LineupException("mensaje");
        assertNull(ex.getField());
        assertEquals("mensaje", ex.getMessage());
    }

    @Test
    void paymentException_singleArg_hasNullField() {
        PaymentException ex = new PaymentException("mensaje");
        assertNull(ex.getField());
        assertEquals("mensaje", ex.getMessage());
    }

    @Test
    void playerException_singleArg_hasNullField() {
        PlayerException ex = new PlayerException("mensaje");
        assertNull(ex.getField());
    }

    @Test
    void playerException_withCause_hasNullField() {
        Throwable cause = new RuntimeException("causa");
        PlayerException ex = new PlayerException("mensaje", cause);
        assertNull(ex.getField());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void refereeException_singleArg_hasNullField() {
        RefereeException ex = new RefereeException("mensaje");
        assertNull(ex.getField());
        assertEquals("mensaje", ex.getMessage());
    }

    @Test
    void teamException_singleArg_hasNullField() {
        TeamException ex = new TeamException("mensaje");
        assertNull(ex.getField());
    }

    @Test
    void teamException_withCause_hasNullField() {
        Throwable cause = new RuntimeException("causa");
        TeamException ex = new TeamException("mensaje", cause);
        assertNull(ex.getField());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void tournamentException_singleArg_hasNullField() {
        TournamentException ex = new TournamentException("mensaje");
        assertNull(ex.getField());
    }

    @Test
    void tournamentException_withCause_hasNullField() {
        Throwable cause = new RuntimeException("causa");
        TournamentException ex = new TournamentException("mensaje", cause);
        assertNull(ex.getField());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void bracketException_constants_notNull() {
        assertNotNull(BracketException.TOURNAMENT_NOT_FOUND);
        assertNotNull(BracketException.BRACKET_ALREADY_EXISTS);
        assertNotNull(BracketException.NOT_ENOUGH_TEAMS);
    }

    @Test
    void teamException_constants_notNull() {
        assertNotNull(TeamException.TEAM_NOT_FOUND);
        assertNotNull(TeamException.PLAYER_ALREADY_HAS_TEAM);
        assertNotNull(TeamException.TEAM_NAME_EMPTY);
    }

    @Test
    void tournamentException_constants_notNull() {
        assertNotNull(TournamentException.TOURNAMENT_NOT_FOUND);
        assertNotNull(TournamentException.NAME_EMPTY);
        assertNotNull(TournamentException.INVALID_STATE_TRANSITION);
    }

    @Test
    void matchException_singleArg_hasNullField() {
        com.techcup.techcup_futbol.core.exception.MatchException ex =
                new com.techcup.techcup_futbol.core.exception.MatchException("mensaje");
        assertNull(ex.getField());
        assertEquals("mensaje", ex.getMessage());
    }

    @Test
    void matchException_constants_notNull() {
        assertNotNull(com.techcup.techcup_futbol.core.exception.MatchException.MATCH_NOT_FOUND);
        assertNotNull(com.techcup.techcup_futbol.core.exception.MatchException.SAME_TEAM);
    }
}
