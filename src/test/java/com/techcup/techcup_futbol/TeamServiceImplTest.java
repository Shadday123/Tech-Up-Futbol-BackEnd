package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.TeamException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.TeamServiceImpl;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TeamServiceImpl Tests")
class TeamServiceImplTest {

    @InjectMocks
    private TeamServiceImpl service;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
            Team t = inv.getArgument(0);
            DataStore.equipos.put(t.getId(), t);
            return t;
        });
        when(teamRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(DataStore.equipos.get(inv.getArgument(0))));
        when(teamRepository.findAll())
                .thenAnswer(inv -> new ArrayList<>(DataStore.equipos.values()));
        doAnswer(inv -> {
            DataStore.equipos.remove(inv.getArgument(0).toString());
            return null;
        }).when(teamRepository).deleteById(anyString());
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-TS-01: createTeam() guarda el equipo en DataStore")
        void createTeamGuardaEnDataStore() {
            Team team = buildTeam("Alpha", buildCaptain("cap1@escuelaing.edu.co", 100001));
            service.createTeam(team);

            assertEquals(1, DataStore.equipos.size());
            assertTrue(DataStore.equipos.containsKey(team.getId()));
        }

        @Test
        @DisplayName("HP-TS-02: createTeam() auto-genera UUID si ID es null")
        void createTeamAutoGeneraId() {
            Team team = buildTeam("Beta", buildCaptain("cap2@escuelaing.edu.co", 100002));
            team.setId(null);
            service.createTeam(team);

            assertNotNull(team.getId());
            assertFalse(team.getId().isBlank());
        }

        @Test
        @DisplayName("HP-TS-03: createTeam() inicializa lista de jugadores si es null")
        void createTeamInicializaListaJugadores() {
            Team team = buildTeam("Gamma", buildCaptain("cap3@escuelaing.edu.co", 100003));
            team.setPlayers(null);
            service.createTeam(team);

            assertNotNull(DataStore.equipos.get(team.getId()).getPlayers());
        }

        @Test
        @DisplayName("HP-TS-04: invitePlayer() agrega jugador al equipo y lo marca con equipo")
        void invitePlayerAgregaJugador() {
            Team team = buildTeam("Delta", buildCaptain("cap4@escuelaing.edu.co", 100004));
            DataStore.equipos.put(team.getId(), team);

            StudentPlayer jugador = buildStudent("inv@escuelaing.edu.co", 200001, "Invitado");
            jugador.setHaveTeam(false);
            DataStore.jugadores.put(jugador.getId(), jugador);

            service.invitePlayer(team.getId(), jugador);

            assertTrue(team.getPlayers().contains(jugador));
            assertTrue(jugador.isHaveTeam());
        }

        @Test
        @DisplayName("HP-TS-06: deleteTeam() elimina equipo del DataStore")
        void deleteTeamEliminaDelDataStore() {
            Team team = buildTeam("Zeta", buildCaptain("cap6@escuelaing.edu.co", 100006));
            DataStore.equipos.put(team.getId(), team);

            service.deleteTeam(team.getId());

            assertFalse(DataStore.equipos.containsKey(team.getId()));
        }

        @Test
        @DisplayName("HP-TS-07: deleteTeam() libera a todos los jugadores del equipo")
        void deleteTeamLiberaJugadores() {
            Team team = buildTeam("Eta", buildCaptain("cap7@escuelaing.edu.co", 100007));
            StudentPlayer j1 = buildStudent("lib1@escuelaing.edu.co", 200003, "Libre1");
            StudentPlayer j2 = buildStudent("lib2@escuelaing.edu.co", 200004, "Libre2");
            j1.setHaveTeam(true);
            j2.setHaveTeam(true);
            team.getPlayers().add(j1);
            team.getPlayers().add(j2);
            DataStore.equipos.put(team.getId(), team);

            service.deleteTeam(team.getId());

            assertFalse(j1.isHaveTeam());
            assertFalse(j2.isHaveTeam());
        }

        @Test
        @DisplayName("HP-TS-08: getAllTeams() retorna todos los equipos")
        void getAllTeamsRetornaTodos() {
            DataStore.equipos.put("E1", buildTeam("Theta", buildCaptain("cap8@escuelaing.edu.co", 100008)));
            DataStore.equipos.put("E2", buildTeam("Iota", buildCaptain("cap9@escuelaing.edu.co", 100009)));

            List<Team> lista = service.getAllTeams();
            assertEquals(2, lista.size());
        }

        @Test
        @DisplayName("HP-TS-09: buscarPorId() retorna Optional con equipo si existe")
        void buscarPorIdRetornaEquipo() {
            Team team = buildTeam("Kappa", buildCaptain("cap10@escuelaing.edu.co", 100010));
            DataStore.equipos.put(team.getId(), team);

            Optional<Team> resultado = service.buscarPorId(team.getId());

            assertTrue(resultado.isPresent());
            assertEquals("Kappa", resultado.get().getTeamName());
        }

        @Test
        @DisplayName("HP-TS-10: obtenerPorId() retorna equipo si existe")
        void obtenerPorIdRetornaEquipo() {
            Team team = buildTeam("Lambda", buildCaptain("cap11@escuelaing.edu.co", 100011));
            DataStore.equipos.put(team.getId(), team);

            Team resultado = service.obtenerPorId(team.getId());
            assertEquals(team.getId(), resultado.getId());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-TS-01: createTeam() lanza TeamException si nombre ya existe")
        void createTeamFallaNombreDuplicado() {
            Team existente = buildTeam("Duplicado", buildCaptain("dup1@escuelaing.edu.co", 300001));
            DataStore.equipos.put(existente.getId(), existente);

            Team nuevo = buildTeam("Duplicado", buildCaptain("dup2@escuelaing.edu.co", 300002));
            TeamException ex = assertThrows(TeamException.class, () -> service.createTeam(nuevo));
            assertEquals("teamName", ex.getField());
        }

        @Test
        @DisplayName("EP-TS-02: createTeam() lanza TeamException si no hay capitán")
        void createTeamFallaSinCapitan() {
            Team team = buildTeam("Sin Capitan", null);
            assertThrows(TeamException.class, () -> service.createTeam(team));
        }

        @Test
        @DisplayName("EP-TS-03: invitePlayer() lanza TeamException si equipo no existe")
        void invitePlayerEquipoNoExiste() {
            StudentPlayer jugador = buildStudent("inv2@escuelaing.edu.co", 400001, "Invitado2");
            jugador.setHaveTeam(false);

            TeamException ex = assertThrows(TeamException.class,
                    () -> service.invitePlayer("NO-EXISTE", jugador));
            assertEquals("id", ex.getField());
        }

        @Test
        @DisplayName("EP-TS-04: invitePlayer() lanza TeamException si jugador ya tiene equipo")
        void invitePlayerJugadorOcupado() {
            Team team = buildTeam("Mu", buildCaptain("cap12@escuelaing.edu.co", 100012));
            DataStore.equipos.put(team.getId(), team);

            StudentPlayer jugador = buildStudent("ocup@escuelaing.edu.co", 400002, "Ocupado");
            jugador.setHaveTeam(true);

            TeamException ex = assertThrows(TeamException.class,
                    () -> service.invitePlayer(team.getId(), jugador));
            assertEquals("player", ex.getField());
        }

        @Test
        @DisplayName("EP-TS-05: invitePlayer() lanza TeamException si equipo está lleno (12 jugadores)")
        void invitePlayerEquipoLleno() {
            Team team = buildTeam("Nu", buildCaptain("cap13@escuelaing.edu.co", 100013));
            for (int i = 0; i < 12; i++) {
                StudentPlayer p = buildStudent("full" + i + "@escuelaing.edu.co", 400100 + i, "Full " + i);
                p.setHaveTeam(false);
                team.getPlayers().add(p);
            }
            DataStore.equipos.put(team.getId(), team);

            StudentPlayer extra = buildStudent("extra@escuelaing.edu.co", 400200, "Extra");
            extra.setHaveTeam(false);

            TeamException ex = assertThrows(TeamException.class,
                    () -> service.invitePlayer(team.getId(), extra));
            assertEquals("players", ex.getField());
        }

        @Test
        @DisplayName("EP-TS-06: removePlayer() lanza TeamException si equipo no existe")
        void removePlayerEquipoNoExiste() {
            assertThrows(TeamException.class,
                    () -> service.removePlayer("NO-EXISTE", "PLAYER-ID"));
        }

        @Test
        @DisplayName("EP-TS-07: removePlayer() lanza TeamException si jugador no está en el equipo")
        void removePlayerJugadorNoEnEquipo() {
            Team team = buildTeam("Xi", buildCaptain("cap14@escuelaing.edu.co", 100014));
            StudentPlayer jugador = buildStudent("nothere@escuelaing.edu.co", 500001, "Not Here");
            team.getPlayers().add(jugador);
            DataStore.equipos.put(team.getId(), team);

            assertThrows(TeamException.class,
                    () -> service.removePlayer(team.getId(), "ID-DISTINTO"));
        }

        @Test
        @DisplayName("EP-TS-08: removePlayer() lanza TeamException si lista de jugadores está vacía")
        void removePlayerListaVacia() {
            Team team = buildTeam("Omicron", buildCaptain("cap15@escuelaing.edu.co", 100015));
            team.setPlayers(new ArrayList<>());
            DataStore.equipos.put(team.getId(), team);

            assertThrows(TeamException.class,
                    () -> service.removePlayer(team.getId(), "CUALQUIER-ID"));
        }

        @Test
        @DisplayName("EP-TS-09: obtenerPorId() lanza TeamException si equipo no existe")
        void obtenerPorIdLanzaExcepcion() {
            TeamException ex = assertThrows(TeamException.class,
                    () -> service.obtenerPorId("NO-EXISTE"));
            assertEquals("id", ex.getField());
            assertTrue(ex.getMessage().contains("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-TS-10: deleteTeam() lanza TeamException si equipo no existe")
        void deleteTeamLanzaExcepcion() {
            assertThrows(TeamException.class,
                    () -> service.deleteTeam("NO-EXISTE"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-TS-01: createTeam() con nombre diferente al existente pasa sin error")
        void createTeamNombreDiferentePasa() {
            Team existente = buildTeam("Equipo A", buildCaptain("a1@escuelaing.edu.co", 600001));
            DataStore.equipos.put(existente.getId(), existente);

            Team nuevo = buildTeam("Equipo B", buildCaptain("a2@escuelaing.edu.co", 600002));
            assertDoesNotThrow(() -> service.createTeam(nuevo));
            assertEquals(2, DataStore.equipos.size());
        }

        @Test
        @DisplayName("CS-TS-02: invitePlayer() en equipo sin lista inicializa la lista")
        void invitePlayerEquipoSinLista() {
            Team team = buildTeam("Pi", buildCaptain("cap16@escuelaing.edu.co", 100016));
            team.setPlayers(null);
            DataStore.equipos.put(team.getId(), team);

            StudentPlayer jugador = buildStudent("nolista@escuelaing.edu.co", 600003, "No Lista");
            jugador.setHaveTeam(false);

            assertDoesNotThrow(() -> service.invitePlayer(team.getId(), jugador));
            assertEquals(1, DataStore.equipos.get(team.getId()).getPlayers().size());
        }

        @Test
        @DisplayName("CS-TS-03: removePlayer() de equipo con un solo jugador deja la lista vacía")
        void removePlayerUnicoJugador() {
            Team team = buildTeam("Rho", buildCaptain("cap17@escuelaing.edu.co", 100017));
            StudentPlayer solo = buildStudent("solo@escuelaing.edu.co", 600004, "Solo");
            solo.setHaveTeam(true);
            team.getPlayers().add(solo);
            // Necesitamos más de 1 jugador para que removePlayer no lance excepción por tamaño mínimo
            // El captain ya estaba en players; la validación require > 1 jugador para remover
            // Usamos un jugador extra para poder remover solo
            StudentPlayer cap = buildCaptain("cap17@escuelaing.edu.co", 100017);
            cap.setHaveTeam(true);
            team.setCaptain(cap);
            // Ponemos team con 2 jugadores: captain + solo
            team.getPlayers().add(cap);
            DataStore.equipos.put(team.getId(), team);

            service.removePlayer(team.getId(), solo.getId());

            assertFalse(team.getPlayers().contains(solo));
            assertFalse(solo.isHaveTeam());
        }

        @Test
        @DisplayName("CS-TS-04: buscarPorId() retorna vacío sin lanzar excepción si no existe")
        void buscarPorIdNoExisteLanzaVacio() {
            Optional<Team> result = service.buscarPorId("NO-EXISTE");
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("CS-TS-05: deleteTeam() con equipo sin jugadores no lanza excepción")
        void deleteTeamSinJugadores() {
            Team team = buildTeam("Sigma", buildCaptain("cap18@escuelaing.edu.co", 100018));
            team.setPlayers(null);
            DataStore.equipos.put(team.getId(), team);

            assertDoesNotThrow(() -> service.deleteTeam(team.getId()));
            assertFalse(DataStore.equipos.containsKey(team.getId()));
        }

        @Test
        @DisplayName("CS-TS-06: getAllTeams() retorna lista vacía si DataStore vacío")
        void getAllTeamsDataStoreVacio() {
            List<Team> lista = service.getAllTeams();
            assertTrue(lista.isEmpty());
        }

        @Test
        @DisplayName("CS-TS-07: createTeam() con nombre case-insensitive diferente no lanza excepción")
        void createTeamNombreCaseDiferente() {
            Team existente = buildTeam("Alfa", buildCaptain("alfa1@escuelaing.edu.co", 700001));
            DataStore.equipos.put(existente.getId(), existente);

            // "ALFA" es duplicado — case-insensitive
            Team dup = buildTeam("ALFA", buildCaptain("alfa2@escuelaing.edu.co", 700002));
            assertThrows(TeamException.class, () -> service.createTeam(dup));
        }
    }

    // ── Helpers

    private StudentPlayer buildCaptain(String email, int numberID) {
        StudentPlayer cap = new StudentPlayer();
        cap.setId(UUID.randomUUID().toString());
        cap.setFullname("Capitan " + numberID);
        cap.setEmail(email);
        cap.setNumberID(numberID);
        cap.setAge(22);
        cap.setGender("Masculino");
        cap.setSemester(4);
        cap.setDorsalNumber(10);
        cap.setPosition(PositionEnum.Midfielder);
        cap.setHaveTeam(false);
        return cap;
    }

    private StudentPlayer buildStudent(String email, int numberID, String name) {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname(name);
        p.setEmail(email);
        p.setNumberID(numberID);
        p.setAge(20);
        p.setGender("Masculino");
        p.setSemester(3);
        p.setDorsalNumber(7);
        p.setPosition(PositionEnum.Defender);
        return p;
    }

    private Team buildTeam(String name, StudentPlayer captain) {
        Team team = new Team();
        team.setId(UUID.randomUUID().toString());
        team.setTeamName(name);
        team.setShieldUrl("shield.png");
        team.setUniformColors("Rojo y Blanco");
        team.setCaptain(captain);
        team.setPlayers(new ArrayList<>());
        return team;
    }
}
