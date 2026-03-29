package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.*;
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
import java.util.stream.Collectors;

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
            RefereeResponse resp = service.create(new CreateRefereeRequest("Árbitro A", "arbitro@example.com"));

            assertNotNull(resp.id());
            assertFalse(resp.id().isBlank());
        }

        @Test
        @DisplayName("HP-REF-02: create() retorna datos correctos del árbitro")
        void createRetornaDatosCorrectos() {
            RefereeResponse resp = service.create(new CreateRefereeRequest("Árbitro B", "b@example.com"));

            assertEquals("Árbitro B", resp.fullname());
            assertEquals("b@example.com", resp.email());
            assertNotNull(resp.assignedMatches());
            assertTrue(resp.assignedMatches().isEmpty());
        }

        @Test
        @DisplayName("HP-REF-03: findById() retorna árbitro existente")
        void findByIdRetornaArbitro() {
            RefereeResponse created = service.create(
                    new CreateRefereeRequest("Árbitro C", "c@example.com"));

            RefereeResponse found = service.findById(created.id());

            assertEquals(created.id(), found.id());
            assertEquals("Árbitro C", found.fullname());
        }

        @Test
        @DisplayName("HP-REF-04: findAll() retorna todos los árbitros registrados")
        void findAllRetornaTodos() {
            service.create(new CreateRefereeRequest("Ref1", "r1@example.com"));
            service.create(new CreateRefereeRequest("Ref2", "r2@example.com"));
            service.create(new CreateRefereeRequest("Ref3", "r3@example.com"));

            List<RefereeResponse> lista = service.findAll();
            assertEquals(3, lista.size());
        }

        @Test
        @DisplayName("HP-REF-05: assignToMatch() asigna árbitro a partido sin árbitro previo")
        void assignToMatchAsignaCorrectamente() {
            RefereeResponse ref = service.create(
                    new CreateRefereeRequest("Árbitro Asig", "asig@example.com"));

            Match match = buildMatch();

            RefereeResponse resp = service.assignToMatch(
                    match.getId(), new AssignRefereeRequest(ref.id()));

            assertEquals(ref.id(), resp.id());
            assertEquals(1, resp.assignedMatches().size());
        }

        @Test
        @DisplayName("HP-REF-06: árbitro puede ser asignado a múltiples partidos distintos")
        void arbitroPuedeAsignarseMúltiplesPartidos() {
            RefereeResponse ref = service.create(
                    new CreateRefereeRequest("Multi Ref", "multi@example.com"));

            Match m1 = buildMatch();
            Match m2 = buildMatch();

            service.assignToMatch(m1.getId(), new AssignRefereeRequest(ref.id()));
            RefereeResponse resp = service.assignToMatch(m2.getId(),
                    new AssignRefereeRequest(ref.id()));

            assertEquals(2, resp.assignedMatches().size());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-REF-01: create() lanza RefereeException si email ya está registrado")
        void createEmailDuplicadoLanza() {
            service.create(new CreateRefereeRequest("Ref Original", "dup@example.com"));

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.create(new CreateRefereeRequest("Ref Dup", "dup@example.com")));
            assertEquals("email", ex.getField());
        }

        @Test
        @DisplayName("EP-REF-02: create() email case-insensitive detecta duplicado")
        void createEmailDuplicadoCaseInsensitiveLanza() {
            service.create(new CreateRefereeRequest("Original", "ref@example.com"));

            assertThrows(RefereeException.class,
                    () -> service.create(new CreateRefereeRequest("Dup Upper",
                            "REF@EXAMPLE.COM")));
        }

        @Test
        @DisplayName("EP-REF-03: assignToMatch() lanza RefereeException si árbitro no existe")
        void assignToMatchArbitroNoExisteLanza() {
            Match match = buildMatch();

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.assignToMatch(match.getId(),
                            new AssignRefereeRequest("NO-EXISTE")));
            assertEquals("refereeId", ex.getField());
        }

        @Test
        @DisplayName("EP-REF-04: assignToMatch() lanza RefereeException si partido no existe")
        void assignToMatchPartidoNoExisteLanza() {
            RefereeResponse ref = service.create(
                    new CreateRefereeRequest("Ref Sin Partido", "sin@example.com"));

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.assignToMatch("NO-EXISTE",
                            new AssignRefereeRequest(ref.id())));
            assertEquals("matchId", ex.getField());
        }

        @Test
        @DisplayName("EP-REF-05: assignToMatch() lanza RefereeException si partido ya tiene árbitro")
        void assignToMatchPartidoYaTieneArbitroLanza() {
            RefereeResponse ref1 = service.create(
                    new CreateRefereeRequest("Ref1 Ya", "ya1@example.com"));
            RefereeResponse ref2 = service.create(
                    new CreateRefereeRequest("Ref2 Ya", "ya2@example.com"));

            Match match = buildMatch();

            service.assignToMatch(match.getId(), new AssignRefereeRequest(ref1.id()));

            RefereeException ex = assertThrows(RefereeException.class,
                    () -> service.assignToMatch(match.getId(),
                            new AssignRefereeRequest(ref2.id())));
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
            String id1 = service.create(
                    new CreateRefereeRequest("R1", "uniq1@example.com")).id();
            String id2 = service.create(
                    new CreateRefereeRequest("R2", "uniq2@example.com")).id();
            String id3 = service.create(
                    new CreateRefereeRequest("R3", "uniq3@example.com")).id();

            assertNotEquals(id1, id2);
            assertNotEquals(id2, id3);
            assertNotEquals(id1, id3);
        }

        @Test
        @DisplayName("CS-REF-03: assignToMatch() modifica la lista de partidos asignados del árbitro")
        void assignToMatchActualizaListaAsignados() {
            RefereeResponse ref = service.create(
                    new CreateRefereeRequest("Lista Ref", "lista@example.com"));

            assertEquals(0, service.findById(ref.id()).assignedMatches().size());

            Match match = buildMatch();
            service.assignToMatch(match.getId(), new AssignRefereeRequest(ref.id()));

            assertEquals(1, service.findById(ref.id()).assignedMatches().size());
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
        t.setUniformColors("Azul");
        t.setPlayers(new ArrayList<>());
        return t;
    }
}
