package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.*;
import com.techcup.techcup_futbol.controller.dto.*;
import com.techcup.techcup_futbol.controller.mapper.PlayerSearchMapper;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.*;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.repository.MatchEventRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllersAndMapperTest {

    // ── Mocks compartidos ──
    @Mock RefereeService refereeService;
    @Mock MatchService matchService;
    @Mock MatchEventRepository matchEventRepository;
    @Mock PaymentService paymentService;
    @Mock TeamRepository teamRepository;
    @Mock LineupService lineupService;
    @Mock PlayerSearchService playerSearchService;
    @Mock BracketService bracketService;
    @Mock TournamentService tournamentService;

    // ── Controllers ──
    @InjectMocks RefereeController refereeController;

    private RefereeController buildRefereeController() {
        return new RefereeController(refereeService);
    }
    private MatchController buildMatchController() {
        return new MatchController(matchService, matchEventRepository);
    }
    private PaymentController buildPaymentController() {
        return new PaymentController(paymentService, teamRepository);
    }
    private LineupController buildLineupController() {
        return new LineupController(lineupService);
    }
    private PlayerSearchController buildPlayerSearchController() {
        return new PlayerSearchController(playerSearchService);
    }
    private BracketController buildBracketController() {
        return new BracketController(bracketService, tournamentService);
    }

    // ── Helpers ──
    private Referee buildReferee() {
        Referee r = new Referee();
        r.setId("ref1");
        r.setFullname("Arbitro Test");
        r.setEmail("arbitro@test.com");
        r.setAssignedMatches(List.of());
        return r;
    }

    private Match buildMatch() {
        Match m = new Match();
        m.setId("m1");
        Team local = new Team(); local.setId("t1"); local.setTeamName("Local");
        Team visitor = new Team(); visitor.setId("t2"); visitor.setTeamName("Visitor");
        m.setLocalTeam(local);
        m.setVisitorTeam(visitor);
        m.setStatus(MatchStatus.SCHEDULED);
        m.setScoreLocal(0);
        m.setScoreVisitor(0);
        m.setYellowCards(0);
        m.setRedCards(0);
        m.setField(1);
        m.setDateTime(LocalDateTime.now());
        return m;
    }

    private Lineup buildLineup() {
        Lineup l = new Lineup();
        l.setId("l1");
        l.setFormation("4-3-3");
        Match m = buildMatch();
        l.setMatch(m);
        Team team = new Team(); team.setId("t1"); team.setTeamName("Local");
        l.setTeam(team);
        l.setStarters(List.of());
        l.setSubstitutes(List.of());
        l.setFieldPositions(List.of());
        return l;
    }

    private Payment buildPayment() {
        Payment p = new Payment();
        p.setId("pay1");
        p.setTeamId("t1");
        p.setAmount(550.0);
        p.setCurrentStatus(PaymentStatus.PENDING);
        p.setReceiptUrl("http://receipt.com");
        return p;
    }

    private Tournament buildTournament() {
        Tournament t = new Tournament();
        t.setId("T1");
        t.setName("Copa Test");
        t.setCurrentState(TournamentState.ACTIVE);
        return t;
    }

    // ════════════════════════════════════════
    // RefereeController
    // ════════════════════════════════════════

    @Test
    void referee_registrar_returnsCreated() {
        RefereeRegistrationRequest req = new RefereeRegistrationRequest(
                "Arbitro", "arb@test.com", "Password1", "LIC-001", 5);
        when(refereeService.registrar(any(), any(), any(), any(), anyInt())).thenReturn(buildReferee());

        ResponseEntity<?> response = buildRefereeController().registrar(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void referee_create_returnsCreated() {
        CreateRefereeRequest req = new CreateRefereeRequest("Arbitro", "arb@test.com");
        when(refereeService.create(any(), any())).thenReturn(buildReferee());

        ResponseEntity<?> response = buildRefereeController().create(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void referee_assignToMatch_returnsOk() {
        AssignRefereeRequest req = new AssignRefereeRequest("ref1");
        when(refereeService.assignToMatch("m1", "ref1")).thenReturn(buildReferee());

        ResponseEntity<?> response = buildRefereeController().assignToMatch("m1", req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void referee_findById_returnsOk() {
        when(refereeService.findById("ref1")).thenReturn(buildReferee());

        ResponseEntity<?> response = buildRefereeController().findById("ref1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void referee_findAll_returnsOk() {
        when(refereeService.findAll()).thenReturn(List.of(buildReferee()));

        ResponseEntity<?> response = buildRefereeController().findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ════════════════════════════════════════
    // MatchController
    // ════════════════════════════════════════

    @Test
    void match_create_returnsCreated() {
        CreateMatchRequest req = new CreateMatchRequest("t1", "t2", LocalDateTime.now(), "ref1", 1);
        Match match = buildMatch();
        when(matchService.create(any(), any(), any(), any(), anyInt())).thenReturn(match);
        when(matchEventRepository.findByMatchId(any())).thenReturn(List.of());

        ResponseEntity<?> response = buildMatchController().create(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void match_registerResult_returnsOk() {
        RegisterResultRequest req = new RegisterResultRequest(2, 1, List.of());
        Match match = buildMatch();
        match.setStatus(MatchStatus.FINISHED);
        match.setScoreLocal(2);
        match.setScoreVisitor(1);
        when(matchService.registerResult(eq("m1"), anyInt(), anyInt(), any())).thenReturn(match);
        when(matchEventRepository.findByMatchId(any())).thenReturn(List.of());

        ResponseEntity<?> response = buildMatchController().registerResult("m1", req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void match_findAll_returnsOk() {
        when(matchService.findAll()).thenReturn(List.of(buildMatch()));
        when(matchEventRepository.findByMatchId(any())).thenReturn(List.of());

        ResponseEntity<?> response = buildMatchController().findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void match_findById_returnsOk() {
        when(matchService.findById("m1")).thenReturn(buildMatch());
        when(matchEventRepository.findByMatchId(any())).thenReturn(List.of());

        ResponseEntity<?> response = buildMatchController().findById("m1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void match_findByTeam_returnsOk() {
        when(matchService.findByTeamId("t1")).thenReturn(List.of(buildMatch()));
        when(matchEventRepository.findByMatchId(any())).thenReturn(List.of());

        ResponseEntity<?> response = buildMatchController().findByTeam("t1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ════════════════════════════════════════
    // PaymentController
    // ════════════════════════════════════════

    @Test
    void payment_uploadReceipt_returnsCreated() {
        UploadReceiptRequest req = new UploadReceiptRequest("t1", "http://receipt.com");
        Payment payment = buildPayment();
        TeamEntity team = new TeamEntity();
        team.setId("t1"); team.setTeamName("Los Mejores");

        when(paymentService.uploadReceipt("t1", "http://receipt.com")).thenReturn(payment);
        when(teamRepository.findById("t1")).thenReturn(Optional.of(team));

        ResponseEntity<?> response = buildPaymentController().uploadReceipt(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void payment_updateStatus_returnsOk() {
        UpdatePaymentStatusRequest req = new UpdatePaymentStatusRequest("APPROVED");
        Payment payment = buildPayment();
        payment.setCurrentStatus(PaymentStatus.APPROVED);

        when(paymentService.updateStatus("pay1", "APPROVED")).thenReturn(payment);
        when(teamRepository.findById("t1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = buildPaymentController().updateStatus("pay1", req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void payment_findById_returnsOk() {
        Payment payment = buildPayment();
        when(paymentService.findById("pay1")).thenReturn(payment);
        when(teamRepository.findById("t1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = buildPaymentController().findById("pay1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void payment_findAll_returnsOk() {
        when(paymentService.findAll()).thenReturn(List.of(buildPayment()));

        ResponseEntity<?> response = buildPaymentController().findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void payment_findByTeam_returnsOk() {
        Payment payment = buildPayment();
        when(paymentService.findByTeamId("t1")).thenReturn(payment);
        when(teamRepository.findById("t1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = buildPaymentController().findByTeam("t1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ════════════════════════════════════════
    // LineupController
    // ════════════════════════════════════════

    @Test
    void lineup_create_returnsCreated() {
        CreateLineupRequest req = new CreateLineupRequest(
                "m1", "t1", "4-3-3",
                List.of("p1","p2","p3","p4","p5","p6","p7"),
                List.of("p8","p9"), List.of());

        when(lineupService.create(any(), any(), any(), any(), any(), any())).thenReturn(buildLineup());

        ResponseEntity<?> response = buildLineupController().create(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void lineup_findByMatchAndTeam_returnsOk() {
        when(lineupService.findByMatchAndTeam("m1", "t1")).thenReturn(buildLineup());

        ResponseEntity<?> response = buildLineupController().findByMatchAndTeam("m1", "t1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void lineup_findRivalLineup_returnsOk() {
        when(lineupService.findRivalLineup("m1", "t1")).thenReturn(buildLineup());

        ResponseEntity<?> response = buildLineupController().findRivalLineup("m1", "t1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ════════════════════════════════════════
    // PlayerSearchController
    // ════════════════════════════════════════

    @Test
    void playerSearch_search_returnsOk() {
        StudentPlayer p = new StudentPlayer();
        p.setId("p1"); p.setFullname("Juan"); p.setSemester(5);
        p.setPosition(PositionEnum.Midfielder); p.setDisponible(true);

        when(playerSearchService.search(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(p));

        ResponseEntity<?> response = buildPlayerSearchController()
                .search(null, null, null, null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ════════════════════════════════════════
    // BracketController
    // ════════════════════════════════════════

    @Test
    void bracket_generate_returnsCreated() {
        GenerateBracketRequest req = new GenerateBracketRequest(4);
        when(bracketService.generate("T1", 4)).thenReturn(List.of());
        when(tournamentService.findById("T1")).thenReturn(buildTournament());

        ResponseEntity<?> response = buildBracketController().generate("T1", req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void bracket_findByTournament_returnsOk() {
        when(bracketService.findByTournamentId("T1")).thenReturn(List.of());
        when(tournamentService.findById("T1")).thenReturn(buildTournament());

        ResponseEntity<?> response = buildBracketController().findByTournament("T1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void bracket_advanceWinner_returnsOk() {
        when(bracketService.advanceWinner("T1", "m1")).thenReturn(List.of());
        when(tournamentService.findById("T1")).thenReturn(buildTournament());

        ResponseEntity<?> response = buildBracketController().advanceWinner("T1", "m1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ════════════════════════════════════════
    // PlayerSearchMapper
    // ════════════════════════════════════════

    @Test
    void playerSearchMapper_studentPlayer_returnsCorrectType() {
        StudentPlayer p = new StudentPlayer();
        p.setId("p1"); p.setFullname("Juan"); p.setSemester(5);
        p.setPosition(PositionEnum.Midfielder); p.setHaveTeam(false);

        PlayerSearchResult result = PlayerSearchMapper.toResult(p);

        assertEquals("STUDENT", result.playerType());
        assertEquals(5, result.semester());
        assertTrue(result.available());
    }

    @Test
    void playerSearchMapper_institutionalPlayer_returnsCorrectType() {
        InstitutionalPlayer p = new InstitutionalPlayer();
        p.setId("p2"); p.setFullname("Maria");
        p.setHaveTeam(true);

        PlayerSearchResult result = PlayerSearchMapper.toResult(p);

        assertEquals("INSTITUTIONAL", result.playerType());
        assertNull(result.semester());
        assertFalse(result.available());
    }
}
