package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.MatchDTOs.*;
import com.techcup.techcup_futbol.core.exception.MatchException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchServiceImpl Tests")
class MatchServiceImplTest {

    @InjectMocks
    private MatchServiceImpl service;

    @Mock
    private LineupService lineupService;

    @Mock
    private StandingsService standingsService;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-MS-01: create() crea partido con ID no nulo y no vacío")
        void createGeneraIdUnico() {
            Team local   = buildTeam("Local A");
            Team visitor = buildTeam("Visitor A");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            MatchResponse resp = service.create(buildRequest(local.getId(), visitor.getId()));

            assertNotNull(resp.id());
            assertFalse(resp.id().isBlank());
        }

        @Test
        @DisplayName("HP-MS-02: create() estado inicial del partido es SCHEDULED")
        void createEstadoInicialScheduled() {
            Team local   = buildTeam("Local B");
            Team visitor = buildTeam("Visitor B");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            MatchResponse resp = service.create(buildRequest(local.getId(), visitor.getId()));

            assertEquals("SCHEDULED", resp.status());
        }

        @Test
        @DisplayName("HP-MS-03: create() invoca lineupService.registerMatch()")
        void createInvocaLineupService() {
            Team local   = buildTeam("Local C");
            Team visitor = buildTeam("Visitor C");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            service.create(buildRequest(local.getId(), visitor.getId()));

            verify(lineupService, times(1)).registerMatch(any(Match.class));
        }

        @Test
        @DisplayName("HP-MS-04: findById() retorna partido existente")
        void findByIdRetornaPartido() {
            Team local   = buildTeam("Local D");
            Team visitor = buildTeam("Visitor D");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            MatchResponse created = service.create(buildRequest(local.getId(), visitor.getId()));
            MatchResponse found   = service.findById(created.id());

            assertEquals(created.id(), found.id());
        }

        @Test
        @DisplayName("HP-MS-05: findAll() retorna todos los partidos")
        void findAllRetornaTodos() {
            Team a = buildTeam("A"); Team b = buildTeam("B"); Team c = buildTeam("C");
            DataStore.equipos.put(a.getId(), a);
            DataStore.equipos.put(b.getId(), b);
            DataStore.equipos.put(c.getId(), c);

            service.create(buildRequest(a.getId(), b.getId()));
            service.create(buildRequest(a.getId(), c.getId()));

            assertEquals(2, service.findAll().size());
        }

        @Test
        @DisplayName("HP-MS-06: findByTeamId() retorna partidos donde participa el equipo")
        void findByTeamIdRetornaPartidos() {
            Team a = buildTeam("A"); Team b = buildTeam("B"); Team c = buildTeam("C");
            DataStore.equipos.put(a.getId(), a);
            DataStore.equipos.put(b.getId(), b);
            DataStore.equipos.put(c.getId(), c);

            service.create(buildRequest(a.getId(), b.getId())); // A participa
            service.create(buildRequest(b.getId(), c.getId())); // B y C, A no participa

            List<MatchResponse> deA = service.findByTeamId(a.getId());
            assertEquals(1, deA.size());
            assertEquals(a.getId(), deA.get(0).localTeamId());
        }

        @Test
        @DisplayName("HP-MS-07: registerResult() registra marcador y estado pasa a FINISHED")
        void registerResultCambiaEstado() {
            Team local   = buildTeam("Local R");
            Team visitor = buildTeam("Visitor R");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();
            MatchResponse resp = service.registerResult(matchId,
                    new RegisterResultRequest(2, 1, null));

            assertEquals("FINISHED", resp.status());
            assertEquals(2, resp.scoreLocal());
            assertEquals(1, resp.scoreVisitor());
        }

        @Test
        @DisplayName("HP-MS-08: registerResult() invoca standingsService.updateFromMatch()")
        void registerResultInvocaStandings() {
            Team local   = buildTeam("Local S");
            Team visitor = buildTeam("Visitor S");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();
            service.registerResult(matchId, new RegisterResultRequest(0, 0, null));

            verify(standingsService, times(1)).updateFromMatch(any(Match.class));
        }

        @Test
        @DisplayName("HP-MS-09: isResultRegistered() retorna true luego de registrar resultado")
        void isResultRegisteredRetornaTrue() {
            Team local   = buildTeam("Local IR");
            Team visitor = buildTeam("Visitor IR");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();
            service.registerResult(matchId, new RegisterResultRequest(1, 0, null));

            assertTrue(service.isResultRegistered(matchId));
        }

        @Test
        @DisplayName("HP-MS-10: isResultRegistered() retorna false para partido sin resultado")
        void isResultRegisteredRetornaFalse() {
            Team local   = buildTeam("Local F");
            Team visitor = buildTeam("Visitor F");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();

            assertFalse(service.isResultRegistered(matchId));
        }

        @Test
        @DisplayName("HP-MS-11: registerMatch() registra partido externo con estado SCHEDULED")
        void registerMatchExternoScheduled() {
            Team local   = buildTeam("Ext L");
            Team visitor = buildTeam("Ext V");

            Match m = new Match();
            m.setId(UUID.randomUUID().toString());
            m.setLocalTeam(local);
            m.setVisitorTeam(visitor);

            service.registerMatch(m);

            assertFalse(service.isResultRegistered(m.getId()));
            assertEquals(1, service.getMatches().size());
        }

        @Test
        @DisplayName("HP-MS-12: registerResult() con eventos GOAL cuenta tarjetas amarillas y rojas")
        void registerResultContadorTarjetas() {
            Team local   = buildTeam("Card L");
            Team visitor = buildTeam("Card V");
            local.setPlayers(new ArrayList<>());
            visitor.setPlayers(new ArrayList<>());

            PlayerAndTeam pat = buildPlayerInTeam(local);
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);
            DataStore.jugadores.put(pat.player.getId(), pat.player);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();

            List<MatchEventRequest> events = List.of(
                    new MatchEventRequest("YELLOW_CARD", 10, pat.player.getId()),
                    new MatchEventRequest("RED_CARD",    20, pat.player.getId())
            );
            MatchResponse resp = service.registerResult(matchId,
                    new RegisterResultRequest(0, 0, events));

            assertEquals(1, resp.yellowCards());
            assertEquals(1, resp.redCards());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-MS-01: create() lanza MatchException si equipo local no existe")
        void createEquipoLocalNoExisteLanza() {
            Team visitor = buildTeam("Visitor X");
            DataStore.equipos.put(visitor.getId(), visitor);

            MatchException ex = assertThrows(MatchException.class,
                    () -> service.create(buildRequest("NO-EXISTE", visitor.getId())));
            assertEquals("localTeamId", ex.getField());
        }

        @Test
        @DisplayName("EP-MS-02: create() lanza MatchException si equipo visitante no existe")
        void createEquipoVisitanteNoExisteLanza() {
            Team local = buildTeam("Local X");
            DataStore.equipos.put(local.getId(), local);

            MatchException ex = assertThrows(MatchException.class,
                    () -> service.create(buildRequest(local.getId(), "NO-EXISTE")));
            assertEquals("visitorTeamId", ex.getField());
        }

        @Test
        @DisplayName("EP-MS-03: create() lanza MatchException si local y visitante son el mismo equipo")
        void createMismoEquipoLanza() {
            Team team = buildTeam("Mismo");
            DataStore.equipos.put(team.getId(), team);

            MatchException ex = assertThrows(MatchException.class,
                    () -> service.create(buildRequest(team.getId(), team.getId())));
            assertEquals("teams", ex.getField());
        }

        @Test
        @DisplayName("EP-MS-04: registerResult() lanza MatchException si partido no existe")
        void registerResultPartidoNoExisteLanza() {
            MatchException ex = assertThrows(MatchException.class,
                    () -> service.registerResult("NO-EXISTE",
                            new RegisterResultRequest(1, 0, null)));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-MS-05: registerResult() lanza MatchException si partido ya tiene resultado")
        void registerResultPartidoYaTerminadoLanza() {
            Team local   = buildTeam("Local Y");
            Team visitor = buildTeam("Visitor Y");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();
            service.registerResult(matchId, new RegisterResultRequest(1, 0, null));

            MatchException ex = assertThrows(MatchException.class,
                    () -> service.registerResult(matchId,
                            new RegisterResultRequest(2, 1, null)));
            assertEquals("status", ex.getField());
        }

        @Test
        @DisplayName("EP-MS-06: findById() lanza MatchException si partido no existe")
        void findByIdNoExisteLanza() {
            MatchException ex = assertThrows(MatchException.class,
                    () -> service.findById("NO-EXISTE"));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-MS-07: registerResult() lanza MatchException si goles no coinciden con eventos GOAL del equipo local")
        void registerResultGoalesMismatchLocalLanza() {
            Team local   = buildTeam("MismatchL");
            Team visitor = buildTeam("MismatchV");
            local.setPlayers(new ArrayList<>());
            visitor.setPlayers(new ArrayList<>());

            PlayerAndTeam pat = buildPlayerInTeam(local);
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);
            DataStore.jugadores.put(pat.player.getId(), pat.player);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();

            // Declaro 2 goles locales pero solo hay 1 evento GOAL del equipo local
            List<MatchEventRequest> events = List.of(
                    new MatchEventRequest("GOAL", 5, pat.player.getId())
            );
            MatchException ex = assertThrows(MatchException.class,
                    () -> service.registerResult(matchId,
                            new RegisterResultRequest(2, 0, events)));
            assertEquals("events", ex.getField());
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-MS-01: findAll() retorna vacío si no hay partidos")
        void findAllRetornaVacio() {
            assertTrue(service.findAll().isEmpty());
        }

        @Test
        @DisplayName("CS-MS-02: findByTeamId() retorna vacío si equipo no tiene partidos")
        void findByTeamIdSinPartidosRetornaVacio() {
            assertTrue(service.findByTeamId("EQUIPO-SIN-PARTIDOS").isEmpty());
        }

        @Test
        @DisplayName("CS-MS-03: create() IDs únicos para partidos distintos")
        void createIdsUnicosParaDistintosPartidos() {
            Team a = buildTeam("A2"); Team b = buildTeam("B2"); Team c = buildTeam("C2");
            DataStore.equipos.put(a.getId(), a);
            DataStore.equipos.put(b.getId(), b);
            DataStore.equipos.put(c.getId(), c);

            String id1 = service.create(buildRequest(a.getId(), b.getId())).id();
            String id2 = service.create(buildRequest(a.getId(), c.getId())).id();

            assertNotEquals(id1, id2);
        }

        @Test
        @DisplayName("CS-MS-04: getMatches() retorna mapa con todos los partidos creados")
        void getMatchesRetornaTodos() {
            Team a = buildTeam("GA"); Team b = buildTeam("GB");
            DataStore.equipos.put(a.getId(), a);
            DataStore.equipos.put(b.getId(), b);

            service.create(buildRequest(a.getId(), b.getId()));
            assertEquals(1, service.getMatches().size());
        }

        @Test
        @DisplayName("CS-MS-05: registerResult() con eventos null no lanza excepción")
        void registerResultEventosNullNoLanza() {
            Team local   = buildTeam("NullEvL");
            Team visitor = buildTeam("NullEvV");
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            String matchId = service.create(buildRequest(local.getId(), visitor.getId())).id();

            assertDoesNotThrow(() ->
                    service.registerResult(matchId, new RegisterResultRequest(0, 0, null)));
        }
    }

    // ── Helpers

    private Team buildTeam(String name) {
        Team t = new Team();
        t.setId(UUID.randomUUID().toString());
        t.setTeamName(name);
        t.setShieldUrl("shield.png");
        t.setUniformColors("Rojo");
        t.setPlayers(new ArrayList<>());
        return t;
    }

    private CreateMatchRequest buildRequest(String localId, String visitorId) {
        return new CreateMatchRequest(localId, visitorId, LocalDateTime.now().plusDays(1), null, 1);
    }

    private record PlayerAndTeam(StudentPlayer player, Team team) {}

    private PlayerAndTeam buildPlayerInTeam(Team team) {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname("Jugador Test");
        p.setAge(20);
        p.setGender("Masculino");
        p.setNumberID(99999);
        p.setEmail("jugador@escuelaing.edu.co");
        p.setDorsalNumber(7);
        p.setPosition(PositionEnum.Midfielder);
        team.getPlayers().add(p);
        return new PlayerAndTeam(p, team);
    }
}
