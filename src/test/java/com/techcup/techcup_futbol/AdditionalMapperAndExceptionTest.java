package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.dto.LineupPlayerDTO;
import com.techcup.techcup_futbol.controller.dto.LineupResponse;
import com.techcup.techcup_futbol.controller.dto.PaymentResponse;
import com.techcup.techcup_futbol.controller.mapper.LineupMapper;
import com.techcup.techcup_futbol.controller.mapper.PaymentMapper;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.exception.*;
import com.techcup.techcup_futbol.exception.GlobalExceptionHandler;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdditionalMapperAndExceptionTest {

    // ── LineupMapper ──

    private Player buildPlayer(String id, String name) {
        StudentPlayer p = new StudentPlayer();
        p.setId(id);
        p.setFullname(name);
        p.setPosition(PositionEnum.Midfielder);
        p.setDorsalNumber(10);
        p.setPhotoUrl("photo.jpg");
        return p;
    }

    private Match buildMatch(String id) {
        Match m = new Match();
        m.setId(id);
        Team local = new Team(); local.setId("t1"); local.setTeamName("Local");
        Team visitor = new Team(); visitor.setId("t2"); visitor.setTeamName("Visitor");
        m.setLocalTeam(local);
        m.setVisitorTeam(visitor);
        return m;
    }

    @Test
    void lineupMapper_toPlayerDTO_mapsCorrectly() {
        Player player = buildPlayer("p1", "Juan");

        LineupPlayerDTO dto = LineupMapper.toPlayerDTO(player);

        assertEquals("p1", dto.playerId());
        assertEquals("Juan", dto.fullname());
        assertEquals("Midfielder", dto.position());
        assertEquals(10, dto.dorsalNumber());
    }

    @Test
    void lineupMapper_toResponse_withNullLists_returnsEmptyLists() {
        Lineup lineup = new Lineup();
        lineup.setId("l1");
        lineup.setFormation("4-4-2");
        lineup.setMatch(buildMatch("m1"));
        Team team = new Team(); team.setId("t1"); team.setTeamName("Los Mejores");
        lineup.setTeam(team);
        lineup.setStarters(null);
        lineup.setSubstitutes(null);
        lineup.setFieldPositions(null);

        LineupResponse response = LineupMapper.toResponse(lineup);

        assertEquals("l1", response.lineupId());
        assertTrue(response.starters().isEmpty());
        assertTrue(response.substitutes().isEmpty());
        assertTrue(response.fieldPositions().isEmpty());
    }

    @Test
    void lineupMapper_toResponse_withData_mapsCorrectly() {
        Lineup lineup = new Lineup();
        lineup.setId("l1");
        lineup.setFormation("4-3-3");
        lineup.setMatch(buildMatch("m1"));
        Team team = new Team(); team.setId("t1"); team.setTeamName("Los Mejores");
        lineup.setTeam(team);
        lineup.setStarters(List.of(buildPlayer("p1", "Juan")));
        lineup.setSubstitutes(List.of(buildPlayer("p2", "Pedro")));
        lineup.setFieldPositions(List.of("p1|50.0|30.0"));

        LineupResponse response = LineupMapper.toResponse(lineup);

        assertEquals(1, response.starters().size());
        assertEquals(1, response.substitutes().size());
        assertEquals(1, response.fieldPositions().size());
        assertEquals(50.0, response.fieldPositions().get(0).x());
        assertEquals(30.0, response.fieldPositions().get(0).y());
    }

    // ── PaymentMapper ──

    @Test
    void paymentMapper_toResponse_withTeam_mapsCorrectly() {
        Payment payment = new Payment();
        payment.setId("pay1");
        payment.setTeamId("t1");
        payment.setReceiptUrl("http://receipt.com/img.jpg");
        payment.setAmount(550.0);
        payment.setCurrentStatus(PaymentStatus.UNDER_REVIEW);

        TeamEntity team = new TeamEntity();
        team.setId("t1");
        team.setTeamName("Los Mejores");

        PaymentResponse response = PaymentMapper.toResponse(payment, team);

        assertEquals("pay1", response.id());
        assertEquals("t1", response.teamId());
        assertEquals("Los Mejores", response.teamName());
        assertEquals(550.0, response.amount());
        assertEquals(PaymentStatus.UNDER_REVIEW, response.currentStatus());
    }

    @Test
    void paymentMapper_toResponse_withNullTeam_mapsNulls() {
        Payment payment = new Payment();
        payment.setId("pay2");
        payment.setAmount(100.0);
        payment.setCurrentStatus(PaymentStatus.PENDING);

        PaymentResponse response = PaymentMapper.toResponse(payment, null);

        assertEquals("pay2", response.id());
        assertNull(response.teamId());
        assertNull(response.teamName());
    }

    // ── Excepciones ──

    @Test
    void bracketException_hasCorrectMessage() {
        BracketException ex = new BracketException("field", "mensaje");
        assertEquals("field", ex.getField());
        assertEquals("mensaje", ex.getMessage());
    }

    @Test
    void lineupException_hasCorrectMessage() {
        LineupException ex = new LineupException("field", "mensaje");
        assertEquals("field", ex.getField());
    }

    @Test
    void matchException_hasCorrectMessage() {
        MatchException ex = new MatchException("field", "mensaje");
        assertEquals("field", ex.getField());
    }

    @Test
    void paymentException_hasCorrectMessage() {
        PaymentException ex = new PaymentException("field", "mensaje");
        assertEquals("field", ex.getField());
    }

    @Test
    void playerException_hasCorrectMessage() {
        PlayerException ex = new PlayerException("field", "mensaje");
        assertEquals("field", ex.getField());
    }

    @Test
    void refereeException_hasCorrectMessage() {
        RefereeException ex = new RefereeException("field", "mensaje");
        assertEquals("field", ex.getField());
    }

    @Test
    void teamException_hasCorrectMessage() {
        TeamException ex = new TeamException("field", "mensaje");
        assertEquals("field", ex.getField());
    }

    @Test
    void tournamentException_hasCorrectMessage() {
        TournamentException ex = new TournamentException("field", "mensaje");
        assertEquals("field", ex.getField());
    }

    @Test
    void resourceNotFoundException_hasCorrectMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("recurso no encontrado");
        assertEquals("recurso no encontrado", ex.getMessage());
    }

    @Test
    void duplicateResourceException_hasCorrectMessage() {
        DuplicateResourceException ex = new DuplicateResourceException("duplicado");
        assertEquals("duplicado", ex.getMessage());
    }

    @Test
    void databaseException_hasCorrectMessage() {
        DatabaseException ex = new DatabaseException("error de base de datos");
        assertEquals("error de base de datos", ex.getMessage());
    }
}
