package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.dto.StandingsResponse;
import com.techcup.techcup_futbol.controller.dto.TournamentDTO;
import com.techcup.techcup_futbol.controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.controller.mapper.StandingsMapper;
import com.techcup.techcup_futbol.controller.mapper.TournamentMapper;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.persistence.entity.*;
import com.techcup.techcup_futbol.persistence.mapper.PlayerPersistenceMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    // ── PlayerPersistenceMapper ──

    @Test
    void playerPersistenceMapper_toEntity_nullReturnsNull() {
        assertNull(PlayerPersistenceMapper.toEntity(null));
    }

    @Test
    void playerPersistenceMapper_toDomain_nullReturnsNull() {
        assertNull(PlayerPersistenceMapper.toDomain(null));
    }

    @Test
    void playerPersistenceMapper_studentPlayer_roundTrip() {
        StudentPlayer player = new StudentPlayer();
        player.setId("p1");
        player.setFullname("Juan");
        player.setEmail("juan@example.com");
        player.setPosition(PositionEnum.Winger);
        player.setSemester(5);
        player.setAge(22);
        player.setGender("M");
        player.setHaveTeam(false);
        player.setDisponible(true);

        PlayerEntity entity = PlayerPersistenceMapper.toEntity(player);
        assertInstanceOf(StudentPlayerEntity.class, entity);
        assertEquals("Juan", entity.getFullname());
        assertEquals(5, ((StudentPlayerEntity) entity).getSemester());

        Player backToDomain = PlayerPersistenceMapper.toDomain(entity);
        assertInstanceOf(StudentPlayer.class, backToDomain);
        assertEquals("Juan", backToDomain.getFullname());
        assertEquals(5, ((StudentPlayer) backToDomain).getSemester());
    }

    @Test
    void playerPersistenceMapper_institutionalPlayer_roundTrip() {
        InstitutionalPlayer player = new InstitutionalPlayer();
        player.setId("p2");
        player.setFullname("Maria");

        PlayerEntity entity = PlayerPersistenceMapper.toEntity(player);
        assertInstanceOf(InstitutionalPlayerEntity.class, entity);

        Player back = PlayerPersistenceMapper.toDomain(entity);
        assertInstanceOf(InstitutionalPlayer.class, back);
        assertEquals("Maria", back.getFullname());
    }

    @Test
    void playerPersistenceMapper_externalPlayer_roundTrip() {
        ExternalPlayer player = new ExternalPlayer();
        player.setId("p3");
        player.setFullname("Pedro");
        player.setRelationship("Familiar");
        player.setRelativeId(1);

        PlayerEntity entity = PlayerPersistenceMapper.toEntity(player);
        assertInstanceOf(ExternalPlayerEntity.class, entity);

        Player back = PlayerPersistenceMapper.toDomain(entity);
        assertInstanceOf(ExternalPlayer.class, back);
    }

    @Test
    void playerPersistenceMapper_relativePlayer_roundTrip() {
        RelativePlayer player = new RelativePlayer();
        player.setId("p4");
        player.setFullname("Ana");
        player.setParentship("Hermano");

        PlayerEntity entity = PlayerPersistenceMapper.toEntity(player);
        assertInstanceOf(RelativePlayerEntity.class, entity);
        assertEquals("Hermano", ((RelativePlayerEntity) entity).getParentship());

        Player back = PlayerPersistenceMapper.toDomain(entity);
        assertInstanceOf(RelativePlayer.class, back);
    }

    @Test
    void playerPersistenceMapper_externalEntity_toDomain() {
        ExternalPlayerEntity entity = new ExternalPlayerEntity();
        entity.setId("p5");
        entity.setFullname("Luis");

        Player result = PlayerPersistenceMapper.toDomain(entity);
        assertInstanceOf(ExternalPlayer.class, result);
    }

    // ── TournamentMapper ──

    @Test
    void tournamentMapper_toModel_nullReturnsNull() {
        assertNull(TournamentMapper.toModel(null));
    }

    @Test
    void tournamentMapper_toDTO_nullReturnsNull() {
        assertNull(TournamentMapper.toDTO(null));
    }

    @Test
    void tournamentMapper_toModel_mapsFields() {
        TournamentDTO dto = new TournamentDTO();
        dto.setId("T001");
        dto.setName("Copa TechUp");
        dto.setStartDate(LocalDateTime.of(2024, 6, 1, 9, 0));
        dto.setEndDate(LocalDateTime.of(2024, 8, 31, 18, 0));
        dto.setMaxTeams(8);
        dto.setRegistrationFee(50000.0);
        dto.setCurrentState(TournamentState.ACTIVE);

        Tournament result = TournamentMapper.toModel(dto);

        assertEquals("T001", result.getId());
        assertEquals("Copa TechUp", result.getName());
        assertEquals(8, result.getMaxTeams());
        assertEquals(TournamentState.ACTIVE, result.getCurrentState());
    }

    @Test
    void tournamentMapper_toModel_withNullId_doesNotSetId() {
        TournamentDTO dto = new TournamentDTO();
        dto.setId(null);
        dto.setName("Sin ID");
        dto.setCurrentState(TournamentState.DRAFT);

        Tournament result = TournamentMapper.toModel(dto);

        assertNull(result.getId());
    }

    @Test
    void tournamentMapper_toDTO_mapsFields() {
        Tournament t = new Tournament();
        t.setId("T002");
        t.setName("Liga Local");
        t.setCurrentState(TournamentState.DRAFT);
        t.setMaxTeams(16);

        TournamentDTO dto = TournamentMapper.toDTO(t);

        assertEquals("T002", dto.getId());
        assertEquals("Liga Local", dto.getName());
        assertEquals(TournamentState.DRAFT, dto.getCurrentState());
    }

    @Test
    void tournamentMapper_toResponse_mapsCorrectly() {
        Tournament t = new Tournament();
        t.setId("T003");
        t.setName("Torneo Final");
        t.setCurrentState(TournamentState.ACTIVE);
        t.setMaxTeams(4);
        t.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        t.setEndDate(LocalDateTime.of(2024, 12, 31, 0, 0));

        TournamentResponse response = TournamentMapper.toResponse(t);

        assertEquals("T003", response.id());
        assertEquals("Torneo Final", response.name());
        assertEquals("ACTIVE", response.currentState());
    }

    @Test
    void tournamentMapper_toConfigResponse_withNullLists_returnsEmptyLists() {
        Tournament t = new Tournament();
        t.setId("T004");
        t.setCurrentState(TournamentState.DRAFT);
        t.setImportantDates(null);
        t.setMatchSchedules(null);
        t.setFields(null);

        var configResponse = TournamentMapper.toConfigResponse(t);

        assertNotNull(configResponse);
        assertTrue(configResponse.importantDates().isEmpty());
        assertTrue(configResponse.matchSchedules().isEmpty());
        assertTrue(configResponse.fields().isEmpty());
    }

    @Test
    void tournamentMapper_toConfigResponse_withData_parsesCorrectly() {
        Tournament t = new Tournament();
        t.setId("T005");
        t.setCurrentState(TournamentState.ACTIVE);
        t.setImportantDates(List.of("Inicio|2024-06-01T09:00:00", "Fin|"));
        t.setMatchSchedules(List.of("Jornada 1|09:00|10:00"));
        t.setFields(List.of("Cancha A|Norte"));

        var configResponse = TournamentMapper.toConfigResponse(t);

        assertEquals(2, configResponse.importantDates().size());
        assertEquals(1, configResponse.matchSchedules().size());
        assertEquals(1, configResponse.fields().size());
    }

    // ── StandingsMapper ──

    @Test
    void standingsMapper_toResponse_buildsCorrectRanking() {
        Team team1 = new Team();
        team1.setId("E1");
        team1.setTeamName("Los Mejores");
        team1.setShieldUrl("shield1.png");

        Team team2 = new Team();
        team2.setId("E2");
        team2.setTeamName("Los Otros");
        team2.setShieldUrl("shield2.png");

        Standings s1 = new Standings();
        s1.setTeam(team1);
        s1.setPoints(9);
        s1.setMatchesPlayed(3);
        s1.setMatchesWon(3);
        s1.setMatchesDrawn(0);
        s1.setMatchesLost(0);
        s1.setGoalsFor(8);
        s1.setGoalsAgainst(2);
        s1.setGoalsDifference(6);

        Standings s2 = new Standings();
        s2.setTeam(team2);
        s2.setPoints(3);
        s2.setMatchesPlayed(3);
        s2.setMatchesWon(1);
        s2.setMatchesDrawn(0);
        s2.setMatchesLost(2);
        s2.setGoalsFor(3);
        s2.setGoalsAgainst(5);
        s2.setGoalsDifference(-2);

        StandingsResponse response = StandingsMapper.toResponse("T001", "Copa TechUp", List.of(s1, s2));

        assertEquals("T001", response.tournamentId());
        assertEquals("Copa TechUp", response.tournamentName());
        assertEquals(2, response.standings().size());
        assertEquals(1, response.standings().get(0).position());
        assertEquals("Los Mejores", response.standings().get(0).teamName());
        assertEquals(2, response.standings().get(1).position());
    }

    @Test
    void standingsMapper_toResponse_emptyList() {
        StandingsResponse response = StandingsMapper.toResponse("T002", "Vacio", List.of());

        assertTrue(response.standings().isEmpty());
    }
}
