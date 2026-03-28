package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.LineupDTOs.*;
import com.techcup.techcup_futbol.core.exception.LineupException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.LineupServiceImpl;
import com.techcup.techcup_futbol.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LineupServiceImpl Tests")
class LineupServiceImplTest {

    @InjectMocks
    private LineupServiceImpl service;

    @Mock
    private LineupRepository lineupRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    private final Map<String, Match> matchStore = new HashMap<>();
    private final Map<String, Lineup> lineupStore = new HashMap<>();

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        matchStore.clear();
        lineupStore.clear();

        // Team repository bridge to DataStore
        when(teamRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(DataStore.equipos.get(inv.getArgument(0))));

        // Player repository bridge to DataStore
        when(playerRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(DataStore.jugadores.get(inv.getArgument(0))));

        // Match repository with local store
        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> {
            Match m = inv.getArgument(0);
            matchStore.put(m.getId(), m);
            return m;
        });
        when(matchRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(matchStore.get(inv.getArgument(0))));
        when(matchRepository.existsById(anyString()))
                .thenAnswer(inv -> matchStore.containsKey(inv.getArgument(0)));

        // Lineup repository with local store
        when(lineupRepository.save(any(Lineup.class))).thenAnswer(inv -> {
            Lineup l = inv.getArgument(0);
            lineupStore.put(l.getId(), l);
            return l;
        });
        when(lineupRepository.findByMatchIdAndTeamId(anyString(), anyString()))
                .thenAnswer(inv -> {
                    String mid = inv.getArgument(0), tid = inv.getArgument(1);
                    return lineupStore.values().stream()
                            .filter(l -> l.getMatch().getId().equals(mid)
                                    && l.getTeam().getId().equals(tid))
                            .findFirst();
                });
        when(lineupRepository.existsByMatchIdAndTeamId(anyString(), anyString()))
                .thenAnswer(inv -> {
                    String mid = inv.getArgument(0), tid = inv.getArgument(1);
                    return lineupStore.values().stream()
                            .anyMatch(l -> l.getMatch().getId().equals(mid)
                                    && l.getTeam().getId().equals(tid));
                });
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-LIN-01: create() guarda alineación con ID no nulo")
        void createGuardaAlineacion() {
            Scenario s = buildScenario();

            LineupResponse resp = service.create(buildRequest(
                    s.match.getId(), s.team.getId(), s.starterIds()));

            assertNotNull(resp.id());
            assertFalse(resp.id().isBlank());
        }

        @Test
        @DisplayName("HP-LIN-02: create() retorna formación, teamId y matchId correctos")
        void createRetornaDatosCorrectos() {
            Scenario s = buildScenario();

            LineupResponse resp = service.create(buildRequest(
                    s.match.getId(), s.team.getId(), s.starterIds()));

            assertEquals(s.match.getId(), resp.matchId());
            assertEquals(s.team.getId(), resp.teamId());
            assertEquals("4-3-3", resp.formation());
        }

        @Test
        @DisplayName("HP-LIN-03: create() retorna exactamente 7 titulares")
        void createRetornaExactamente7Titulares() {
            Scenario s = buildScenario();

            LineupResponse resp = service.create(buildRequest(
                    s.match.getId(), s.team.getId(), s.starterIds()));

            assertEquals(7, resp.starters().size());
        }

        @Test
        @DisplayName("HP-LIN-04: findByMatchAndTeam() retorna alineación existente")
        void findByMatchAndTeamRetornaAlineacion() {
            Scenario s = buildScenario();
            service.create(buildRequest(s.match.getId(), s.team.getId(), s.starterIds()));

            LineupResponse found = service.findByMatchAndTeam(
                    s.match.getId(), s.team.getId());

            assertNotNull(found);
            assertEquals(s.team.getId(), found.teamId());
        }

        @Test
        @DisplayName("HP-LIN-05: findRivalLineup() retorna alineación del rival")
        void findRivalLineupRetornaRival() {
            Team local   = buildTeam("Local Rival");
            Team visitor = buildTeam("Visitor Rival");
            Match match  = buildMatch(local, visitor);
            service.registerMatch(match);
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            List<String> localStarters   = addPlayersToTeam(local, 7);
            List<String> visitorStarters = addPlayersToTeam(visitor, 7);

            service.create(buildRequest(match.getId(), local.getId(), localStarters));
            service.create(buildRequest(match.getId(), visitor.getId(), visitorStarters));

            LineupResponse rivalResp = service.findRivalLineup(
                    match.getId(), local.getId());

            assertEquals(visitor.getId(), rivalResp.teamId());
        }

        @Test
        @DisplayName("HP-LIN-06: create() con suplentes los incluye en la respuesta")
        void createConSuplentes() {
            Scenario s = buildScenario();
            List<String> suplentes = addPlayersToTeam(s.team, 3);

            CreateLineupRequest req = new CreateLineupRequest(
                    s.match.getId(), s.team.getId(), "4-3-3",
                    s.starterIds(), suplentes, null);

            LineupResponse resp = service.create(req);

            assertEquals(3, resp.substitutes().size());
        }

        @Test
        @DisplayName("HP-LIN-07: dos equipos pueden tener alineación en el mismo partido")
        void dosEquiposMismoPartidoConviven() {
            Team local   = buildTeam("L Conviven");
            Team visitor = buildTeam("V Conviven");
            Match match  = buildMatch(local, visitor);
            service.registerMatch(match);
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            List<String> localStarters   = addPlayersToTeam(local, 7);
            List<String> visitorStarters = addPlayersToTeam(visitor, 7);

            assertDoesNotThrow(() -> {
                service.create(buildRequest(match.getId(), local.getId(), localStarters));
                service.create(buildRequest(match.getId(), visitor.getId(), visitorStarters));
            });

            assertNotNull(service.findByMatchAndTeam(match.getId(), local.getId()));
            assertNotNull(service.findByMatchAndTeam(match.getId(), visitor.getId()));
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-LIN-01: create() lanza LineupException si partido no existe en el servicio")
        void createPartidoNoExisteLanza() {
            Team team = buildTeam("Team NoMatch");
            DataStore.equipos.put(team.getId(), team);
            List<String> starters = addPlayersToTeam(team, 7);

            LineupException ex = assertThrows(LineupException.class,
                    () -> service.create(buildRequest("NO-EXISTE-MATCH",
                            team.getId(), starters)));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-02: create() lanza LineupException si equipo no existe en DataStore")
        void createEquipoNoExisteLanza() {
            Match match = buildMatch(buildTeam("L"), buildTeam("V"));
            service.registerMatch(match);

            LineupException ex = assertThrows(LineupException.class,
                    () -> service.create(buildRequest(match.getId(),
                            "NO-EXISTE-TEAM", List.of())));
            assertEquals("teamId", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-03: create() lanza LineupException si alineación ya existe para el partido y equipo")
        void createAlineacionDuplicadaLanza() {
            Scenario s = buildScenario();
            service.create(buildRequest(s.match.getId(), s.team.getId(), s.starterIds()));

            List<String> otrosStarters = addPlayersToTeam(s.team, 7);
            LineupException ex = assertThrows(LineupException.class,
                    () -> service.create(buildRequest(s.match.getId(),
                            s.team.getId(), otrosStarters)));
            assertEquals("lineup", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-04: create() lanza LineupException si titulares != 7")
        void createTitularesMenosDeSieteLanza() {
            Scenario s = buildScenario();
            List<String> pocosStarters = s.starterIds().subList(0, 5);

            LineupException ex = assertThrows(LineupException.class,
                    () -> service.create(buildRequest(s.match.getId(),
                            s.team.getId(), pocosStarters)));
            assertEquals("starters", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-05: create() lanza LineupException si starterIds es null")
        void createStartersNullLanza() {
            Scenario s = buildScenario();

            CreateLineupRequest req = new CreateLineupRequest(
                    s.match.getId(), s.team.getId(), "4-3-3",
                    null, null, null);

            LineupException ex = assertThrows(LineupException.class,
                    () -> service.create(req));
            assertEquals("starters", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-06: create() lanza LineupException si titular no pertenece al equipo")
        void createTitularFueraDelEquipoLanza() {
            Scenario s = buildScenario();

            List<String> startIds = new ArrayList<>(s.starterIds().subList(0, 6));
            StudentPlayer foraneo = buildPlayer();
            DataStore.jugadores.put(foraneo.getId(), foraneo);
            startIds.add(foraneo.getId()); // no está en el equipo

            LineupException ex = assertThrows(LineupException.class,
                    () -> service.create(buildRequest(s.match.getId(),
                            s.team.getId(), startIds)));
            assertEquals("starters", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-07: findByMatchAndTeam() lanza LineupException si no existe")
        void findByMatchAndTeamNoExisteLanza() {
            LineupException ex = assertThrows(LineupException.class,
                    () -> service.findByMatchAndTeam("NO-MATCH", "NO-TEAM"));
            assertEquals("lineup", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-08: findRivalLineup() lanza LineupException si partido no registrado")
        void findRivalLineupPartidoNoExisteLanza() {
            LineupException ex = assertThrows(LineupException.class,
                    () -> service.findRivalLineup("NO-EXISTE", "ANY-TEAM"));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-LIN-09: findRivalLineup() lanza LineupException si rival no tiene alineación")
        void findRivalLineupSinAlineacionRivalLanza() {
            Team local   = buildTeam("L Sin Rival");
            Team visitor = buildTeam("V Sin Rival");
            Match match  = buildMatch(local, visitor);
            service.registerMatch(match);
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            List<String> localStarters = addPlayersToTeam(local, 7);
            service.create(buildRequest(match.getId(), local.getId(), localStarters));

            LineupException ex = assertThrows(LineupException.class,
                    () -> service.findRivalLineup(match.getId(), local.getId()));
            assertEquals("lineup", ex.getField());
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-LIN-01: create() sin suplentes retorna lista vacía de suplentes")
        void createSinSuplentesRetornaVacio() {
            Scenario s = buildScenario();

            CreateLineupRequest req = new CreateLineupRequest(
                    s.match.getId(), s.team.getId(), "4-3-3",
                    s.starterIds(), null, null);

            LineupResponse resp = service.create(req);
            assertTrue(resp.substitutes().isEmpty());
        }

        @Test
        @DisplayName("CS-LIN-02: create() con posiciones de campo las incluye en la respuesta")
        void createConPosicionesEnCampo() {
            Scenario s = buildScenario();
            List<PlayerPositionDTO> positions = List.of(
                    new PlayerPositionDTO(s.starterIds().get(0), 10.5, 20.3)
            );

            CreateLineupRequest req = new CreateLineupRequest(
                    s.match.getId(), s.team.getId(), "4-3-3",
                    s.starterIds(), null, positions);

            LineupResponse resp = service.create(req);
            assertEquals(1, resp.fieldPositions().size());
        }

        @Test
        @DisplayName("CS-LIN-03: registerMatch() permite crear alineación para ese partido")
        void registerMatchHabilitaCrearAlineacion() {
            Team team = buildTeam("Reg Match Team");
            Match match = buildMatch(team, buildTeam("Oponente"));
            DataStore.equipos.put(team.getId(), team);
            List<String> starters = addPlayersToTeam(team, 7);

            service.registerMatch(match);

            assertDoesNotThrow(() ->
                    service.create(buildRequest(match.getId(), team.getId(), starters)));
        }

        @Test
        @DisplayName("CS-LIN-04: findRivalLineup() es simétrico — local pregunta por visitor y viceversa")
        void findRivalLineupSimetrico() {
            Team local   = buildTeam("L Sim");
            Team visitor = buildTeam("V Sim");
            Match match  = buildMatch(local, visitor);
            service.registerMatch(match);
            DataStore.equipos.put(local.getId(), local);
            DataStore.equipos.put(visitor.getId(), visitor);

            List<String> localStarters   = addPlayersToTeam(local, 7);
            List<String> visitorStarters = addPlayersToTeam(visitor, 7);
            service.create(buildRequest(match.getId(), local.getId(), localStarters));
            service.create(buildRequest(match.getId(), visitor.getId(), visitorStarters));

            LineupResponse rivalDeLocal   = service.findRivalLineup(match.getId(), local.getId());
            LineupResponse rivalDeVisitor = service.findRivalLineup(match.getId(), visitor.getId());

            assertEquals(visitor.getId(), rivalDeLocal.teamId());
            assertEquals(local.getId(), rivalDeVisitor.teamId());
        }
    }

    // ── Helpers y Builders

    private record Scenario(Match match, Team team, List<String> starterIds) {}

    private Scenario buildScenario() {
        Team team = buildTeam("Equipo Test");
        Team rival = buildTeam("Rival");
        Match match = buildMatch(team, rival);
        service.registerMatch(match);
        DataStore.equipos.put(team.getId(), team);
        List<String> starters = addPlayersToTeam(team, 7);
        return new Scenario(match, team, starters);
    }

    private Match buildMatch(Team local, Team visitor) {
        Match m = new Match();
        m.setId(UUID.randomUUID().toString());
        m.setLocalTeam(local);
        m.setVisitorTeam(visitor);
        m.setDateTime(LocalDateTime.now().plusDays(1));
        m.setField(1);
        return m;
    }

    private Team buildTeam(String name) {
        Team t = new Team();
        t.setId(UUID.randomUUID().toString());
        t.setTeamName(name);
        t.setShieldUrl("shield.png");
        t.setUniformColors("Azul");
        t.setPlayers(new ArrayList<>());
        return t;
    }

    private StudentPlayer buildPlayer() {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname("Jugador " + UUID.randomUUID().toString().substring(0, 4));
        p.setAge(20);
        p.setGender("Masculino");
        p.setNumberID(new Random().nextInt(999999));
        p.setEmail(UUID.randomUUID() + "@escuelaing.edu.co");
        p.setDorsalNumber(new Random().nextInt(99));
        p.setPosition(PositionEnum.Midfielder);
        p.setSemester(4);
        return p;
    }

    private List<String> addPlayersToTeam(Team team, int count) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            StudentPlayer p = buildPlayer();
            team.getPlayers().add(p);
            DataStore.jugadores.put(p.getId(), p);
            ids.add(p.getId());
        }
        return ids;
    }

    private CreateLineupRequest buildRequest(String matchId, String teamId,
                                             List<String> starterIds) {
        return new CreateLineupRequest(matchId, teamId, "4-3-3", starterIds, null, null);
    }
}
