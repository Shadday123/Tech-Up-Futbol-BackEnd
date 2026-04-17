package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.dto.BracketMatchDTO;
import com.techcup.techcup_futbol.controller.dto.BracketResponse;
import com.techcup.techcup_futbol.controller.dto.PhaseDTO;
import com.techcup.techcup_futbol.controller.mapper.BracketMapper;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.entity.TournamentBracketsEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BracketMapperTest {

    private Tournament tournament;
    private TeamEntity localTeamEntity;
    private TeamEntity visitorTeamEntity;
    private MatchEntity matchEntity;
    private TournamentBracketsEntity bracketEntity;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        tournament.setId("t-001");
        tournament.setName("Copa TechUp");

        localTeamEntity = new TeamEntity();
        localTeamEntity.setId("team-local");
        localTeamEntity.setTeamName("Dragons");

        visitorTeamEntity = new TeamEntity();
        visitorTeamEntity.setId("team-visitor");
        visitorTeamEntity.setTeamName("Hawks");

        matchEntity = new MatchEntity();
        matchEntity.setId("match-001");
        matchEntity.setLocalTeam(localTeamEntity);
        matchEntity.setVisitorTeam(visitorTeamEntity);
        matchEntity.setStatus(MatchStatus.SCHEDULED);

        bracketEntity = new TournamentBracketsEntity();
        bracketEntity.setId("bracket-001");
        bracketEntity.setPhase(PhaseEnum.INITIAL_ROUND);
        bracketEntity.setMatches(List.of(matchEntity));
    }

    @Test
    void toResponse_withValidData_returnsCorrectResponse() {
        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        assertNotNull(response);
        assertEquals("t-001", response.tournamentId());
        assertEquals("Copa TechUp", response.tournamentName());
        assertEquals(1, response.phases().size());
    }

    @Test
    void toResponse_withNullPhases_returnsEmptyPhases() {
        BracketResponse response = BracketMapper.toResponse("t-001", tournament, null);

        assertNotNull(response);
        assertTrue(response.phases().isEmpty());
    }

    @Test
    void toResponse_withEmptyPhases_returnsEmptyPhases() {
        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of());

        assertNotNull(response);
        assertTrue(response.phases().isEmpty());
    }

    @Test
    void toResponse_phase_hasCorrectPhaseName() {
        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        PhaseDTO phase = response.phases().get(0);
        assertEquals("INITIAL_ROUND", phase.phase());
    }

    @Test
    void toResponse_match_hasCorrectTeamNames() {
        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        BracketMatchDTO match = response.phases().get(0).matches().get(0);
        assertEquals("Dragons", match.localTeamName());
        assertEquals("Hawks", match.visitorTeamName());
        assertEquals("SCHEDULED", match.status());
    }

    @Test
    void toResponse_scheduledMatch_scoresAreNull() {
        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        BracketMatchDTO match = response.phases().get(0).matches().get(0);
        assertNull(match.scoreLocal());
        assertNull(match.scoreVisitor());
    }

    @Test
    void toResponse_finishedMatch_scoresArePresent() {
        matchEntity.setStatus(MatchStatus.FINISHED);
        matchEntity.setScoreLocal(2);
        matchEntity.setScoreVisitor(1);

        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        BracketMatchDTO match = response.phases().get(0).matches().get(0);
        assertEquals(2, match.scoreLocal());
        assertEquals(1, match.scoreVisitor());
    }

    @Test
    void toResponse_matchWithWinner_winnerIsSet() {
        matchEntity.setStatus(MatchStatus.FINISHED);
        matchEntity.setWinner(localTeamEntity);

        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        BracketMatchDTO match = response.phases().get(0).matches().get(0);
        assertEquals("Dragons", match.winnerName());
        assertEquals("team-local", match.winnerId());
    }

    @Test
    void toResponse_matchWithNullStatus_defaultsToScheduled() {
        matchEntity.setStatus(null);

        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        BracketMatchDTO match = response.phases().get(0).matches().get(0);
        assertEquals("SCHEDULED", match.status());
    }

    @Test
    void toResponse_matchWithNullLocalTeam_localTeamFieldsAreNull() {
        matchEntity.setLocalTeam(null);

        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        BracketMatchDTO match = response.phases().get(0).matches().get(0);
        assertNull(match.localTeamName());
        assertNull(match.localTeamId());
    }

    @Test
    void toResponse_matchWithNullVisitorTeam_visitorTeamFieldsAreNull() {
        matchEntity.setVisitorTeam(null);

        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        BracketMatchDTO match = response.phases().get(0).matches().get(0);
        assertNull(match.visitorTeamName());
        assertNull(match.visitorTeamId());
    }

    @Test
    void toResponse_bracketWithNullMatches_phaseHasEmptyMatches() {
        bracketEntity.setMatches(null);

        BracketResponse response = BracketMapper.toResponse("t-001", tournament, List.of(bracketEntity));

        PhaseDTO phase = response.phases().get(0);
        assertTrue(phase.matches().isEmpty());
    }

    @Test
    void toResponseFromModels_withValidData_returnsCorrectResponse() {
        Match match = new Match();
        match.setId("match-model-001");
        Team local = new Team();
        local.setId("lm");
        local.setTeamName("ModelDragons");
        Team visitor = new Team();
        visitor.setId("vm");
        visitor.setTeamName("ModelHawks");
        match.setLocalTeam(local);
        match.setVisitorTeam(visitor);
        match.setStatus(MatchStatus.SCHEDULED);

        TournamentBrackets bracket = new TournamentBrackets();
        bracket.setPhase(PhaseEnum.QUARTER_FINALS);
        bracket.setMatches(List.of(match));

        BracketResponse response = BracketMapper.toResponseFromModels("t-001", tournament, List.of(bracket));

        assertNotNull(response);
        assertEquals("t-001", response.tournamentId());
        assertEquals(1, response.phases().size());
        assertEquals("QUARTER_FINALS", response.phases().get(0).phase());
    }

    @Test
    void toResponseFromModels_withNullPhases_returnsEmptyPhases() {
        BracketResponse response = BracketMapper.toResponseFromModels("t-001", tournament, null);
        assertTrue(response.phases().isEmpty());
    }

    @Test
    void toResponseFromModels_finishedMatch_hasScores() {
        Match match = new Match();
        match.setId("m001");
        match.setStatus(MatchStatus.FINISHED);
        match.setScoreLocal(3);
        match.setScoreVisitor(0);
        Team winner = new Team();
        winner.setId("w1");
        winner.setTeamName("Winners");
        match.setWinner(winner);

        TournamentBrackets bracket = new TournamentBrackets();
        bracket.setPhase(PhaseEnum.FINAL);
        bracket.setMatches(List.of(match));

        BracketResponse response = BracketMapper.toResponseFromModels("t-001", tournament, List.of(bracket));

        BracketMatchDTO dto = response.phases().get(0).matches().get(0);
        assertEquals(3, dto.scoreLocal());
        assertEquals(0, dto.scoreVisitor());
        assertEquals("Winners", dto.winnerName());
    }

    @Test
    void toResponseFromModels_matchWithNullTeams_fieldsAreNull() {
        Match match = new Match();
        match.setId("m001");
        match.setStatus(MatchStatus.SCHEDULED);
        match.setLocalTeam(null);
        match.setVisitorTeam(null);

        TournamentBrackets bracket = new TournamentBrackets();
        bracket.setPhase(PhaseEnum.SEMI_FINALS);
        bracket.setMatches(List.of(match));

        BracketResponse response = BracketMapper.toResponseFromModels("t-001", tournament, List.of(bracket));

        BracketMatchDTO dto = response.phases().get(0).matches().get(0);
        assertNull(dto.localTeamName());
        assertNull(dto.visitorTeamName());
        assertNull(dto.winnerId());
    }

    @Test
    void toResponseFromModels_bracketWithNullMatches_phaseHasEmptyMatches() {
        TournamentBrackets bracket = new TournamentBrackets();
        bracket.setPhase(PhaseEnum.INITIAL_ROUND);
        bracket.setMatches(null);

        BracketResponse response = BracketMapper.toResponseFromModels("t-001", tournament, List.of(bracket));

        assertTrue(response.phases().get(0).matches().isEmpty());
    }

    @Test
    void toResponseFromModels_matchWithNullStatus_defaultsToScheduled() {
        Match match = new Match();
        match.setId("m001");
        match.setStatus(null);

        TournamentBrackets bracket = new TournamentBrackets();
        bracket.setPhase(PhaseEnum.INITIAL_ROUND);
        bracket.setMatches(List.of(match));

        BracketResponse response = BracketMapper.toResponseFromModels("t-001", tournament, List.of(bracket));

        assertEquals("SCHEDULED", response.phases().get(0).matches().get(0).status());
    }

    @Test
    void multiplePhasesArePreservedInOrder() {
        TournamentBracketsEntity bracket2 = new TournamentBracketsEntity();
        bracket2.setId("bracket-002");
        bracket2.setPhase(PhaseEnum.QUARTER_FINALS);
        bracket2.setMatches(List.of());

        BracketResponse response = BracketMapper.toResponse("t-001", tournament,
                List.of(bracketEntity, bracket2));

        assertEquals(2, response.phases().size());
        assertEquals("INITIAL_ROUND", response.phases().get(0).phase());
        assertEquals("QUARTER_FINALS", response.phases().get(1).phase());
    }
}
