package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.StandingsServiceImpl;
import com.techcup.techcup_futbol.repository.StandingsRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StandingsServiceImpl Tests")
class StandingsServiceImplTest {

    @InjectMocks
    private StandingsServiceImpl service;

    @Mock
    private StandingsRepository standingsRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    private final Map<String, Standings> standingsStore = new HashMap<>();

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        standingsStore.clear();

        when(tournamentRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(DataStore.torneos.get(inv.getArgument(0, String.class))));

        when(standingsRepository.save(any(Standings.class))).thenAnswer(inv -> {
            Standings s = inv.getArgument(0);
            standingsStore.put(s.getId(), s);
            return s;
        });

        when(standingsRepository.findByTournamentIdAndTeamId(anyString(), anyString())).thenAnswer(inv -> {
            String tId = inv.getArgument(0);
            String teamId = inv.getArgument(1);
            return standingsStore.values().stream()
                    .filter(s -> s.getTournamentId().equals(tId) && s.getTeam().getId().equals(teamId))
                    .findFirst();
        });

        when(standingsRepository.findByTournamentId(anyString())).thenAnswer(inv -> {
            String tId = inv.getArgument(0);
            return standingsStore.values().stream()
                    .filter(s -> s.getTournamentId().equals(tId))
                    .collect(Collectors.toList());
        });
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-STD-01: registerTeamInTournament() registra equipo en tabla de posiciones")
        void registerTeamRegistraEnTabla() {
            Tournament torneo = buildTorneo("T001");
            Team equipo = buildEquipo("Equipo A");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.registerTeamInTournament(torneo.getId().toString(), equipo);
            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());

            assertEquals(1, standings.size());
            assertEquals("Equipo A", standings.get(0).getTeam().getTeamName());
        }

        @Test
        @DisplayName("HP-STD-02: registerTeamInTournament() no duplica si se llama dos veces con mismo equipo")
        void registerTeamNoDuplica() {
            Tournament torneo = buildTorneo("T002");
            Team equipo = buildEquipo("Equipo B");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.registerTeamInTournament(torneo.getId().toString(), equipo);
            service.registerTeamInTournament(torneo.getId().toString(), equipo); // segunda vez

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            assertEquals(1, standings.size());
        }

        @Test
        @DisplayName("HP-STD-03: updateFromMatch() suma 3 puntos al equipo que gana")
        void updateFromMatchSumaPuntosGanador() {
            Tournament torneo = buildTorneo("T003");
            Team local   = buildEquipo("Local Win");
            Team visitor = buildEquipo("Visitor Lose");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.registerTeamInTournament(torneo.getId().toString(), local);
            service.registerTeamInTournament(torneo.getId().toString(), visitor);

            Match match = buildPartido(local, visitor, 2, 0);
            service.updateFromMatch(match);

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            var localRow = standings.stream()
                    .filter(s -> s.getTeam().getTeamName().equals("Local Win")).findFirst().orElseThrow();
            assertEquals(3, localRow.getPoints());
            assertEquals(1, localRow.getMatchesWon());
        }

        @Test
        @DisplayName("HP-STD-04: updateFromMatch() suma 1 punto a ambos equipos en empate")
        void updateFromMatchSumaPuntoEmpate() {
            Tournament torneo = buildTorneo("T004");
            Team local   = buildEquipo("Empate Local");
            Team visitor = buildEquipo("Empate Visitor");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.registerTeamInTournament(torneo.getId().toString(), local);
            service.registerTeamInTournament(torneo.getId().toString(), visitor);

            Match match = buildPartido(local, visitor, 1, 1);
            service.updateFromMatch(match);

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            standings.forEach(s -> assertEquals(1, s.getPoints()));
        }

        @Test
        @DisplayName("HP-STD-05: updateFromMatch() no suma puntos al equipo que pierde")
        void updateFromMatchNoPuntosAlPerdedor() {
            Tournament torneo = buildTorneo("T005");
            Team local   = buildEquipo("Ganador");
            Team visitor = buildEquipo("Perdedor");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.registerTeamInTournament(torneo.getId().toString(), local);
            service.registerTeamInTournament(torneo.getId().toString(), visitor);

            Match match = buildPartido(local, visitor, 3, 1);
            service.updateFromMatch(match);

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            var perdedor = standings.stream()
                    .filter(s -> s.getTeam().getTeamName().equals("Perdedor")).findFirst().orElseThrow();
            assertEquals(0, perdedor.getPoints());
            assertEquals(1, perdedor.getMatchesLost());
        }

        @Test
        @DisplayName("HP-STD-06: findByTournamentId() retorna lista de standings correctamente")
        void findByTournamentIdRetornaCorrectamente() {
            Tournament torneo = buildTorneo("T006");
            Team equipo = buildEquipo("Equipo Test");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.registerTeamInTournament(torneo.getId().toString(), equipo);

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            assertNotNull(standings);
            assertEquals(1, standings.size());
            assertEquals(torneo.getId(), standings.get(0).getTournamentId());
        }

        @Test
        @DisplayName("HP-STD-07: findByTournamentId() retorna tabla vacía si no se registraron equipos")
        void findByTournamentIdTablaVacia() {
            Tournament torneo = buildTorneo("T007");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            assertTrue(standings.isEmpty());
        }

        @Test
        @DisplayName("HP-STD-08: updateFromMatch() actualiza goles correctamente")
        void updateFromMatchActualizaGoles() {
            Tournament torneo = buildTorneo("T008");
            Team local   = buildEquipo("Goleador");
            Team visitor = buildEquipo("Goleado");
            DataStore.torneos.put(torneo.getId().toString(), torneo);

            service.registerTeamInTournament(torneo.getId().toString(), local);
            service.registerTeamInTournament(torneo.getId().toString(), visitor);

            Match match = buildPartido(local, visitor, 4, 1);
            service.updateFromMatch(match);

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            var goleador = standings.stream()
                    .filter(s -> s.getTeam().getTeamName().equals("Goleador")).findFirst().orElseThrow();
            assertEquals(4, goleador.getGoalsFor());
            assertEquals(1, goleador.getGoalsAgainst());
            assertEquals(3, goleador.getGoalsDifference());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-STD-01: findByTournamentId() lanza TournamentException si torneo no existe")
        void findByTournamentIdNoExisteLanza() {
            TournamentException ex = assertThrows(TournamentException.class,
                    () -> service.findByTournamentId("NO-EXISTE"));
            assertEquals("id", ex.getField());
        }

        @Test
        @DisplayName("EP-STD-02: updateFromMatch() no lanza excepción si equipo no está registrado")
        void updateFromMatchEquipoNoRegistradoNoLanza() {
            Team local   = buildEquipo("Sin Registro L");
            Team visitor = buildEquipo("Sin Registro V");
            Match match = buildPartido(local, visitor, 1, 0);

            assertDoesNotThrow(() -> service.updateFromMatch(match));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-STD-01: standings ordenados por puntos descendente")
        void standingsOrdenadosPorPuntos() {
            Tournament torneo = buildTorneo("T010");
            Team a = buildEquipo("A"); Team b = buildEquipo("B"); Team c = buildEquipo("C");
            DataStore.torneos.put(torneo.getId().toString(), torneo);
            service.registerTeamInTournament(torneo.getId().toString(), a);
            service.registerTeamInTournament(torneo.getId().toString(), b);
            service.registerTeamInTournament(torneo.getId().toString(), c);

            service.updateFromMatch(buildPartido(a, b, 2, 0)); // A gana 3 pts
            service.updateFromMatch(buildPartido(b, c, 1, 0)); // B gana 3 pts
            service.updateFromMatch(buildPartido(a, c, 1, 1)); // A y C 1 pt

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            // A=4pts, B=3pts, C=1pt
            assertEquals("A", standings.get(0).getTeam().getTeamName());
        }

        @Test
        @DisplayName("CS-STD-02: múltiples partidos acumulan partidos jugados")
        void multiplesPartidosAcumulan() {
            Tournament torneo = buildTorneo("T011");
            Team equipo = buildEquipo("Acumulador");
            Team rival  = buildEquipo("Rival");
            DataStore.torneos.put(torneo.getId().toString(), torneo);
            service.registerTeamInTournament(torneo.getId().toString(), equipo);
            service.registerTeamInTournament(torneo.getId().toString(), rival);

            service.updateFromMatch(buildPartido(equipo, rival, 1, 0));
            service.updateFromMatch(buildPartido(rival, equipo, 0, 2));

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            var acum = standings.stream()
                    .filter(s -> s.getTeam().getTeamName().equals("Acumulador")).findFirst().orElseThrow();
            assertEquals(2, acum.getMatchesPlayed());
            assertEquals(6, acum.getPoints());
        }

        @Test
        @DisplayName("CS-STD-03: registerTeamInTournament() puede registrar múltiples equipos en un torneo")
        void registerMultiplesEquipos() {
            Tournament torneo = buildTorneo("T012");
            DataStore.torneos.put(torneo.getId().toString(), torneo);
            for (int i = 1; i <= 4; i++) {
                service.registerTeamInTournament(torneo.getId().toString(), buildEquipo("Equipo " + i));
            }

            List<Standings> standings = service.findByTournamentId(torneo.getId().toString());
            assertEquals(4, standings.size());
        }
    }

    // ── Helpers

    private Tournament buildTorneo(String id) {
        Tournament t = new Tournament();
        t.setId(id);
        t.setName("Torneo " + id);
        t.setStartDate(LocalDateTime.now().plusDays(5));
        t.setEndDate(LocalDateTime.now().plusDays(30));
        t.setCurrentState(TournamentState.IN_PROGRESS);
        t.setMaxTeams(8);
        return t;
    }

    private Team buildEquipo(String name) {
        Team team = new Team();
        team.setId(UUID.randomUUID().toString());
        team.setTeamName(name);
        team.setShieldUrl("shield.png");
        team.setUniformColors(Collections.singletonList("Azul"));
        team.setPlayers(new ArrayList<>());
        return team;
    }

    private Match buildPartido(Team local, Team visitor, int scoreLocal, int scoreVisitor) {
        Match m = new Match();
        m.setId(UUID.randomUUID().toString());
        m.setLocalTeam(local);
        m.setVisitorTeam(visitor);
        m.setScoreLocal(scoreLocal);
        m.setScoreVisitor(scoreVisitor);
        return m;
    }
}
