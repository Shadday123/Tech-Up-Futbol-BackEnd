package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.RefereeException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.RefereeServiceImpl;
import com.techcup.techcup_futbol.repository.MatchRepository;
import com.techcup.techcup_futbol.repository.RefereeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RefereeServiceImpl Tests")
class RefereeServiceImplTest {

    @InjectMocks
    private RefereeServiceImpl service;

    @Mock
    private RefereeRepository refereeRepository;

    @Mock
    private MatchRepository matchRepository;

    private final Map<String, Referee> refereeStore = new HashMap<>();
    private final Map<String, Match> matchStore = new HashMap<>();

    @BeforeEach
    void setUp() {
        refereeStore.clear();
        matchStore.clear();

        when(refereeRepository.save(any(Referee.class))).thenAnswer(inv -> {
            Referee r = inv.getArgument(0);
            refereeStore.put(r.getId(), r);
            return r;
        });

        when(refereeRepository.findById(anyString())).thenAnswer(inv ->
                Optional.ofNullable(refereeStore.get(inv.getArgument(0, String.class))));

        when(refereeRepository.findAll()).thenAnswer(inv ->
                new ArrayList<>(refereeStore.values()));

        when(refereeRepository.existsByEmail(anyString())).thenAnswer(inv -> {
            String email = inv.getArgument(0, String.class);
            return refereeStore.values().stream()
                    .anyMatch(r -> r.getEmail().equalsIgnoreCase(email));
        });

        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> {
            Match m = inv.getArgument(0);
            matchStore.put(m.getId(), m);
            return m;
        });

        when(matchRepository.findById(anyString())).thenAnswer(inv ->
                Optional.ofNullable(matchStore.get(inv.getArgument(0, String.class))));
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-REF-01: create() registra árbitro con ID no nulo")
        void createRegistraArbitroConId() {
            Referee resp = service.create("Árbitro A", "arbitro@example.com");

            assertNotNull(resp.getId());
            assertFalse(resp.getId().isBlank());
        }

        @Test
        @DisplayName("HP-REF-02: create() retorna datos correctos del árbitro")
        void createRetornaDatosCorrectos() {
            Referee resp = service.create("Árbitro B", "b@example.com");

            assertEquals("Árbitro B", resp.getFullname());
            assertEquals("b@example.com", resp.getEmail());
            assertNotNull(resp.getAssignedMatches());
            assertTrue(resp.getAssignedMatches().isEmpty());
        }

        @Test
        @DisplayName("HP-REF-03: findById() retorna árbitro existente")
        void findByIdRetornaArbitro() {
            Referee created = service.create("Árbitro C", "c@example.com");

            Referee found = service.findById(created.getId());

            assertEquals(created.getId(), found.getId());
            assertEquals("Árbitro C", found.getFullname());
        }

        @Test
        @DisplayName("HP-REF-04: findAll() retorna todos los árbitros registrados")
        void findAllRetornaTodos() {
            service.create("Ref1", "r1@example.com");
            service.create("Ref2", "r2@example.com");
            service.create("Ref3", "r3@example.com");

            List<Referee> lista = service.findAll();
            assertEquals(3, lista.size());
        }

        @Test
        @DisplayName("HP-REF-05: assignToMatch() asigna árbitro a partido sin árbitro previo")
        void assignToMatchAsignaCorrectamente() {
            Referee ref = service.create("Árbitro Asig", "asig@example.com");

            Match match = buildMatch();

            Referee resp = service.assignToMatch(match.getId(), ref.getId());

            assertEquals(ref.getId(), resp.getId());
            assertEquals(1, resp.getAssignedMatches().size());
        }

        @Test
        @DisplayName("HP-REF-06: árbitro puede ser asignado a múltiples partidos distintos")
        void arbitroPuedeAsignarseMúltiplesPartidos() {
            Referee ref = service.create("Multi Ref", "multi@example.com");

            Match m1 = buildMatch();
            Match m2 = buildMatch();

            service.assignToMatch(m1.getId(), ref.getId());
            Referee resp = service.assignToMatch(m2.getId(), ref.getId());

            assertEquals(2, resp.getAssignedMatches().size());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-REF-01: create() lanza RefereeException si email ya está registrado")
        void createEmailDuplicadoLanza() {
            service.create("Ref Original", "dup@example.com");

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.create("Ref Dup", "dup@example.com"));
            assertEquals("email", ex.getField());
        }

        @Test
        @DisplayName("EP-REF-02: create() email case-insensitive detecta duplicado")
        void createEmailDuplicadoCaseInsensitiveLanza() {
            service.create("Original", "ref@example.com");

            assertThrows(RefereeException.class,
                    () -> service.create("Dup Upper", "REF@EXAMPLE.COM"));
        }

        @Test
        @DisplayName("EP-REF-03: assignToMatch() lanza RefereeException si árbitro no existe")
        void assignToMatchArbitroNoExisteLanza() {
            Match match = buildMatch();

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.assignToMatch(match.getId(), "NO-EXISTE"));
            assertEquals("refereeId", ex.getField());
        }

        @Test
        @DisplayName("EP-REF-04: assignToMatch() lanza RefereeException si partido no existe")
        void assignToMatchPartidoNoExisteLanza() {
            Referee ref = service.create("Ref Sin Partido", "sin@example.com");

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.assignToMatch("NO-EXISTE", ref.getId()));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-REF-05: assignToMatch() lanza RefereeException si partido ya tiene árbitro")
        void assignToMatchPartidoYaTieneArbitroLanza() {
            Referee ref1 = service.create("Ref1 Ya", "ya1@example.com");
            Referee ref2 = service.create("Ref2 Ya", "ya2@example.com");

            Match match = buildMatch();

            service.assignToMatch(match.getId(), ref1.getId());

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.assignToMatch(match.getId(), ref2.getId()));
            assertEquals("match", ex.getField());
        }

        @Test
        @DisplayName("EP-REF-06: findById() lanza RefereeException si árbitro no existe")
        void findByIdNoExisteLanza() {
            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.findById("NO-EXISTE"));
            assertEquals("id", ex.getField());
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-REF-01: findAll() retorna vacío si no hay árbitros")
        void findAllVacioSinArbitros() {
            assertTrue(service.findAll().isEmpty());
        }

        @Test
        @DisplayName("CS-REF-02: múltiples árbitros tienen IDs únicos")
        void multiplesArbitrosIdsUnicos() {
            String id1 = service.create("R1", "uniq1@example.com").getId();
            String id2 = service.create("R2", "uniq2@example.com").getId();
            String id3 = service.create("R3", "uniq3@example.com").getId();

            assertNotEquals(id1, id2);
            assertNotEquals(id2, id3);
            assertNotEquals(id1, id3);
        }

        @Test
        @DisplayName("CS-REF-03: assignToMatch() modifica la lista de partidos asignados del árbitro")
        void assignToMatchActualizaListaAsignados() {
            Referee ref = service.create("Lista Ref", "lista@example.com");

            assertEquals(0, service.findById(ref.getId()).getAssignedMatches().size());

            Match match = buildMatch();
            service.assignToMatch(match.getId(), ref.getId());

            assertEquals(1, service.findById(ref.getId()).getAssignedMatches().size());
        }
    }

    // ── Helpers

    private Match buildMatch() {
        Team local   = buildTeam("Local");
        Team visitor = buildTeam("Visitor");

        Match m = new Match();
        m.setId(UUID.randomUUID().toString());
        m.setLocalTeam(local);
        m.setVisitorTeam(visitor);
        m.setDateTime(LocalDateTime.now().plusDays(1));
        m.setField(1);
        matchStore.put(m.getId(), m);
        return m;
    }

    private Team buildTeam(String name) {
        Team t = new Team();
        t.setId(UUID.randomUUID().toString());
        t.setTeamName(name);
        t.setShieldUrl("shield.png");
        t.setUniformColors(Collections.singletonList("Azul"));
        t.setPlayers(new ArrayList<>());
        return t;
    }
}
