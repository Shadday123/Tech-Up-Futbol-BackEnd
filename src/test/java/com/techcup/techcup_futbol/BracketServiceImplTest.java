package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.*;
import com.techcup.techcup_futbol.core.exception.BracketException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.BracketServiceImpl;
import com.techcup.techcup_futbol.core.service.MatchService;
import com.techcup.techcup_futbol.repository.MatchRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import com.techcup.techcup_futbol.repository.TournamentBracketsRepository;
import com.techcup.techcup_futbol.repository.TournamentRepository;
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
@DisplayName("BracketServiceImpl Tests")
class BracketServiceImplTest {

    @InjectMocks
    private BracketServiceImpl service;

    @Mock
    private MatchService matchService;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TournamentBracketsRepository tournamentBracketsRepository;

    @Mock
    private MatchRepository matchRepository;

    private final Map<String, Match> matchStore = new HashMap<>();
    private final Map<String, TournamentBrackets> bracketStore = new HashMap<>();

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        matchStore.clear();
        bracketStore.clear();

        when(tournamentRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(DataStore.torneos.get(inv.getArgument(0, String.class))));

        when(teamRepository.findAll())
                .thenAnswer(inv -> new ArrayList<>(DataStore.equipos.values()));

        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> {
            Match m = inv.getArgument(0);
            matchStore.put(m.getId(), m);
            return m;
        });

        when(matchRepository.findById(anyString())).thenAnswer(inv ->
                Optional.ofNullable(matchStore.get(inv.getArgument(0, String.class))));

        when(tournamentBracketsRepository.save(any(TournamentBrackets.class))).thenAnswer(inv -> {
            TournamentBrackets b = inv.getArgument(0);
            bracketStore.put(b.getId(), b);
            return b;
        });

        when(tournamentBracketsRepository.findByTournamentId(anyString())).thenAnswer(inv -> {
            String tId = inv.getArgument(0, String.class);
            return bracketStore.values().stream()
                    .filter(b -> b.getTournament().getId().equals(tId))
                    .collect(Collectors.toList());
        });

        doAnswer(inv -> {
            List<TournamentBrackets> toDelete = inv.getArgument(0);
            toDelete.forEach(b -> bracketStore.remove(b.getId()));
            return null;
        }).when(tournamentBracketsRepository).deleteAll(anyList());
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-BRK-01: generate() con 2 equipos usa fase FINAL")
        void generateDosEquiposFaseFinal() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse resp = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(2));

            assertEquals(1, resp.phases().size());
            assertEquals("FINAL", resp.phases().get(0).phase());
            assertEquals(1, resp.phases().get(0).matches().size());
        }

        @Test
        @DisplayName("HP-BRK-02: generate() con 4 equipos usa fase SEMI_FINALS con 2 partidos")
        void generateCuatroEquiposSemifinales() {
            Tournament torneo = buildTorneo();
            addTeams(4);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse resp = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(4));

            assertEquals("SEMI_FINALS", resp.phases().get(0).phase());
            assertEquals(2, resp.phases().get(0).matches().size());
        }

        @Test
        @DisplayName("HP-BRK-03: generate() con 8 equipos usa fase QUARTER_FINALS con 4 partidos")
        void generateOchoEquiposCuartos() {
            Tournament torneo = buildTorneo();
            addTeams(8);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse resp = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(8));

            assertEquals("QUARTER_FINALS", resp.phases().get(0).phase());
            assertEquals(4, resp.phases().get(0).matches().size());
        }

        @Test
        @DisplayName("HP-BRK-04: generate() retorna tournamentId y nombre correctos")
        void generateRetornaDatosCorrectos() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse resp = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(2));

            assertEquals(torneo.getId(), resp.tournamentId());
            assertEquals(torneo.getName(), resp.tournamentName());
        }

        @Test
        @DisplayName("HP-BRK-05: findByTournamentId() retorna llaves generadas")
        void findByTournamentIdRetornaLlaves() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.generate(torneo.getId().toString(), new GenerateBracketRequest(2));
            BracketResponse resp = service.findByTournamentId(torneo.getId().toString());

            assertNotNull(resp);
            assertFalse(resp.phases().isEmpty());
        }

        @Test
        @DisplayName("HP-BRK-06: advanceWinner() determina ganador cuando local gana")
        void advanceWinnerLocalGana() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse bracket = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(2));
            String matchId = bracket.phases().get(0).matches().get(0).matchId();

            setMatchScore(matchId, 2, 0);
            when(matchService.isResultRegistered(matchId)).thenReturn(true);

            BracketResponse resp = service.advanceWinner(torneo.getId().toString(), matchId);

            BracketMatchDTO matchDTO = resp.phases().get(0).matches().get(0);
            assertNotNull(matchDTO.winnerId());
            assertEquals("FINISHED", matchDTO.status());
        }

        @Test
        @DisplayName("HP-BRK-07: generate() invoca matchService.registerMatch() por cada partido")
        void generateInvocaRegisterMatch() {
            Tournament torneo = buildTorneo();
            addTeams(4);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.generate(torneo.getId().toString(), new GenerateBracketRequest(4));

            verify(matchService, times(2)).registerMatch(any(Match.class));
        }

        @Test
        @DisplayName("HP-BRK-08: generate() puede regenerar si no hay resultados registrados")
        void generatePuedeRegenerarSinResultados() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.generate(torneo.getId().toString(), new GenerateBracketRequest(2));

            assertDoesNotThrow(() ->
                    service.generate(torneo.getId().toString(), new GenerateBracketRequest(2)));
        }

        @Test
        @DisplayName("HP-BRK-09: advanceWinner() visitante gana si su marcador es mayor")
        void advanceWinnerVisitanteGana() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse bracket = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(2));
            String matchId = bracket.phases().get(0).matches().get(0).matchId();

            setMatchScore(matchId, 0, 3);
            when(matchService.isResultRegistered(matchId)).thenReturn(true);

            BracketResponse resp = service.advanceWinner(torneo.getId().toString(), matchId);

            BracketMatchDTO matchDTO = resp.phases().get(0).matches().get(0);
            // El ganador debe ser el visitante
            assertEquals(bracket.phases().get(0).matches().get(0).visitorTeamId(),
                    matchDTO.winnerId());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-BRK-01: generate() lanza BracketException si torneo no existe")
        void generateTorneoNoExisteLanza() {
            BracketException ex = assertThrows(BracketException.class,
                    () -> service.generate("NO-EXISTE", new GenerateBracketRequest(4)));
            assertEquals("tournamentId", ex.getField());
        }

        @Test
        @DisplayName("EP-BRK-02: generate() lanza BracketException si no hay suficientes equipos (< 2)")
        void generatePocoEquiposLanza() {
            Tournament torneo = buildTorneo();
            addTeams(1);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketException ex = assertThrows(BracketException.class,
                    () -> service.generate(torneo.getId().toString(), new GenerateBracketRequest(4)));
            assertEquals("teams", ex.getField());
        }

        @Test
        @DisplayName("EP-BRK-03: generate() lanza BracketException si equipos no son potencia de 2")
        void generateNoPotenciaDeDosLanza() {
            Tournament torneo = buildTorneo();
            addTeams(6);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketException ex = assertThrows(BracketException.class,
                    () -> service.generate(torneo.getId().toString(), new GenerateBracketRequest(6)));
            assertEquals("teams", ex.getField());
        }

        @Test
        @DisplayName("EP-BRK-04: findByTournamentId() lanza BracketException si torneo no existe")
        void findByTournamentIdTorneoNoExisteLanza() {
            BracketException ex = assertThrows(BracketException.class,
                    () -> service.findByTournamentId("NO-EXISTE"));
            assertEquals("tournamentId", ex.getField());
        }

        @Test
        @DisplayName("EP-BRK-05: findByTournamentId() lanza BracketException si no hay llaves generadas")
        void findByTournamentIdSinLlavesLanza() {
            Tournament torneo = buildTorneo();
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketException ex = assertThrows(BracketException.class,
                    () -> service.findByTournamentId(torneo.getId().toString()));
            assertEquals("bracket", ex.getField());
        }

        @Test
        @DisplayName("EP-BRK-06: advanceWinner() lanza BracketException si torneo no existe")
        void advanceWinnerTorneoNoExisteLanza() {
            assertThrows(BracketException.class,
                    () -> service.advanceWinner("NO-EXISTE", "ANY-MATCH"));
        }

        @Test
        @DisplayName("EP-BRK-07: advanceWinner() lanza BracketException si partido no está en llaves")
        void advanceWinnerPartidoNoExisteLanza() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);
            service.generate(torneo.getId().toString(), new GenerateBracketRequest(2));

            BracketException ex = assertThrows(BracketException.class,
                    () -> service.advanceWinner(torneo.getId().toString(), "NO-EXISTE-MATCH"));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-BRK-08: advanceWinner() lanza BracketException si partido sin resultado registrado")
        void advanceWinnerSinResultadoLanza() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse bracket = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(2));
            String matchId = bracket.phases().get(0).matches().get(0).matchId();

            when(matchService.isResultRegistered(matchId)).thenReturn(false);

            BracketException ex = assertThrows(BracketException.class,
                    () -> service.advanceWinner(torneo.getId().toString(), matchId));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-BRK-09: advanceWinner() lanza BracketException si partido terminó en empate")
        void advanceWinnerEmpateNoTieneGanadorLanza() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse bracket = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(2));
            String matchId = bracket.phases().get(0).matches().get(0).matchId();

            setMatchScore(matchId, 1, 1); // empate
            when(matchService.isResultRegistered(matchId)).thenReturn(true);

            BracketException ex = assertThrows(BracketException.class,
                    () -> service.advanceWinner(torneo.getId().toString(), matchId));
            assertEquals("match", ex.getField());
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-BRK-01: partidos iniciales tienen estado SCHEDULED")
        void generatePartidosInicialesScheduled() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse resp = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(2));

            resp.phases().get(0).matches()
                    .forEach(m -> assertEquals("SCHEDULED", m.status()));
        }

        @Test
        @DisplayName("CS-BRK-02: IDs de partidos generados son únicos entre sí")
        void generateIdsPartidosUnicos() {
            Tournament torneo = buildTorneo();
            addTeams(4);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            BracketResponse resp = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(4));

            List<String> ids = resp.phases().get(0).matches()
                    .stream().map(BracketMatchDTO::matchId).toList();
            assertEquals(ids.size(), new HashSet<>(ids).size());
        }

        @Test
        @DisplayName("CS-BRK-03: generate() usa como máximo la cantidad de equipos disponibles")
        void generateUsaMaxEquiposDisponibles() {
            Tournament torneo = buildTorneo();
            addTeams(2);
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            // Pedimos 4 pero solo hay 2 → debe usar min(4,2)=2 (FINAL)
            BracketResponse resp = service.generate(torneo.getId().toString(),
                    new GenerateBracketRequest(4));

            assertEquals("FINAL", resp.phases().get(0).phase());
        }
    }

    // ── Helpers

    private Tournament buildTorneo() {
        Tournament t = new Tournament();
        t.setId("torneo-test");
        t.setName("Torneo Test");
        t.setStartDate(LocalDateTime.now().plusDays(5));
        t.setEndDate(LocalDateTime.now().plusDays(30));
        t.setCurrentState(TournamentState.IN_PROGRESS);
        t.setMaxTeams(8);
        return t;
    }

    private void addTeams(int count) {
        for (int i = 0; i < count; i++) {
            Team t = new Team();
            t.setId(UUID.randomUUID().toString());
            t.setTeamName("Equipo " + (i + 1));
            t.setShieldUrl("shield.png");
            t.setUniformColors("Rojo");
            t.setPlayers(new ArrayList<>());
            DataStore.equipos.put(t.getId(), t);
        }
    }

    private void setMatchScore(String matchId, int scoreLocal, int scoreVisitor) {
        Match match = matchStore.get(matchId);
        match.setScoreLocal(scoreLocal);
        match.setScoreVisitor(scoreVisitor);
    }
}
