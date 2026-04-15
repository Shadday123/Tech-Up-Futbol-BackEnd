package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.EliminationStrategy;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.RandomStrategy;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrategyAndUtilTest {

    // ── EliminationStrategy ──

    private Team buildTeam(String id, String name) {
        Team t = new Team();
        t.setId(id);
        t.setTeamName(name);
        return t;
    }

    @Test
    void eliminationStrategy_4Teams_generates2Matches() {
        List<Team> teams = List.of(
                buildTeam("T1", "Alpha"), buildTeam("T2", "Beta"),
                buildTeam("T3", "Gamma"), buildTeam("T4", "Delta")
        );

        List<Match> matches = new EliminationStrategy().generateMatches(teams);

        assertEquals(2, matches.size());
        // T1 vs T4 y T2 vs T3
        assertEquals("Alpha", matches.get(0).getLocalTeam().getTeamName());
        assertEquals("Delta", matches.get(0).getVisitorTeam().getTeamName());
    }

    @Test
    void eliminationStrategy_nullTeams_returnsEmpty() {
        List<Match> matches = new EliminationStrategy().generateMatches(null);

        assertTrue(matches.isEmpty());
    }

    @Test
    void eliminationStrategy_oneTeam_returnsEmpty() {
        List<Match> matches = new EliminationStrategy().generateMatches(
                List.of(buildTeam("T1", "Solo")));

        assertTrue(matches.isEmpty());
    }

    @Test
    void eliminationStrategy_oddTeams_ignoresLastTeam() {
        List<Team> teams = List.of(
                buildTeam("T1", "A"), buildTeam("T2", "B"), buildTeam("T3", "C")
        );

        List<Match> matches = new EliminationStrategy().generateMatches(teams);

        assertEquals(1, matches.size());
    }

    // ── RandomStrategy ──

    @Test
    void randomStrategy_4Teams_generates2Matches() {
        List<Team> teams = new ArrayList<>();
        teams.add(buildTeam("T1", "A"));
        teams.add(buildTeam("T2", "B"));
        teams.add(buildTeam("T3", "C"));
        teams.add(buildTeam("T4", "D"));

        List<Match> matches = new RandomStrategy().generateMatches(teams);

        assertEquals(2, matches.size());
        for (Match m : matches) {
            assertNotNull(m.getLocalTeam());
            assertNotNull(m.getVisitorTeam());
            assertNotEquals(m.getLocalTeam().getId(), m.getVisitorTeam().getId());
        }
    }

    @Test
    void randomStrategy_emptyList_returnsEmpty() {
        List<Match> matches = new RandomStrategy().generateMatches(new ArrayList<>());

        assertTrue(matches.isEmpty());
    }

    @Test
    void randomStrategy_2Teams_generates1Match() {
        List<Team> teams = new ArrayList<>();
        teams.add(buildTeam("T1", "A"));
        teams.add(buildTeam("T2", "B"));

        List<Match> matches = new RandomStrategy().generateMatches(teams);

        assertEquals(1, matches.size());
    }

    // ── IdGenerator ──

    @Test
    void idGenerator_generateId_returnsNonNull() {
        String id = IdGenerator.generateId();

        assertNotNull(id);
        assertFalse(id.isBlank());
    }

    @Test
    void idGenerator_generateId_returnsUniqueIds() {
        String id1 = IdGenerator.generateId();
        String id2 = IdGenerator.generateId();

        assertNotEquals(id1, id2);
    }

    @Test
    void idGenerator_generateId_isUuidFormat() {
        String id = IdGenerator.generateId();
        // UUID tiene 36 caracteres: 8-4-4-4-12
        assertEquals(36, id.length());
        assertTrue(id.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
}
