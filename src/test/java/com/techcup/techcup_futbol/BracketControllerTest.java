package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.BracketController;
import com.techcup.techcup_futbol.Controller.dto.BracketDTOs.*;
import com.techcup.techcup_futbol.core.exception.BracketException;
import com.techcup.techcup_futbol.core.service.BracketService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BracketController Tests")
class BracketControllerTest {

    @Mock
    private BracketService bracketService;

    private BracketController controller;

    @BeforeEach
    void setUp() {
        controller = new BracketController(bracketService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-BC-01: generate() retorna 201 CREATED con el bracket generado")
        void generateRetorna201() {
            GenerateBracketRequest req = new GenerateBracketRequest(4);
            BracketResponse resp = buildResponse("T001", "Torneo Test");
            when(bracketService.generate("T001", req)).thenReturn(resp);

            ResponseEntity<BracketResponse> response = controller.generate("T001", req);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("T001", response.getBody().tournamentId());
        }

        @Test
        @DisplayName("HP-BC-02: generate() llama al servicio con tournamentId y request correctos")
        void generateLlamaServicio() {
            GenerateBracketRequest req = new GenerateBracketRequest(8);
            when(bracketService.generate("T002", req)).thenReturn(buildResponse("T002", "Torneo 2"));

            controller.generate("T002", req);
            verify(bracketService, times(1)).generate("T002", req);
        }

        @Test
        @DisplayName("HP-BC-03: findByTournament() retorna 200 OK con el bracket existente")
        void findByTournamentRetorna200() {
            BracketResponse resp = buildResponse("T001", "Torneo Find");
            when(bracketService.findByTournamentId("T001")).thenReturn(resp);

            ResponseEntity<BracketResponse> response = controller.findByTournament("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Torneo Find", response.getBody().tournamentName());
        }

        @Test
        @DisplayName("HP-BC-04: advanceWinner() retorna 200 OK con el bracket actualizado")
        void advanceWinnerRetorna200() {
            BracketResponse resp = buildResponse("T001", "Torneo Advance");
            when(bracketService.advanceWinner("T001", "M001")).thenReturn(resp);

            ResponseEntity<BracketResponse> response = controller.advanceWinner("T001", "M001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-BC-01: generate() propaga BracketException si torneo no existe")
        void generatePropagaExcepcionTorneoNoExiste() {
            GenerateBracketRequest req = new GenerateBracketRequest(4);
            doThrow(new BracketException("tournamentId",
                    BracketException.TOURNAMENT_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(bracketService).generate("NO-EXISTE", req);

            assertThrows(BracketException.class, () -> controller.generate("NO-EXISTE", req));
        }

        @Test
        @DisplayName("EP-BC-02: generate() propaga BracketException si equipos no son potencia de 2")
        void generatePropagaExcepcionEquiposNoPotenciaDos() {
            GenerateBracketRequest req = new GenerateBracketRequest(3);
            doThrow(new BracketException("teams",
                    BracketException.TEAMS_NOT_POWER_OF_TWO.formatted(3)))
                    .when(bracketService).generate("T001", req);

            BracketException ex = assertThrows(BracketException.class,
                    () -> controller.generate("T001", req));
            assertEquals("teams", ex.getField());
        }

        @Test
        @DisplayName("EP-BC-03: generate() propaga BracketException si hay resultados ya registrados")
        void generatePropagaExcepcionResultadosRegistrados() {
            GenerateBracketRequest req = new GenerateBracketRequest(4);
            doThrow(new BracketException("bracket", BracketException.RESULTS_ALREADY_REGISTERED))
                    .when(bracketService).generate("T001", req);

            assertThrows(BracketException.class, () -> controller.generate("T001", req));
        }

        @Test
        @DisplayName("EP-BC-04: findByTournament() propaga BracketException si no hay bracket")
        void findByTournamentPropagaExcepcion() {
            doThrow(new BracketException("bracket",
                    BracketException.BRACKET_NOT_FOUND.formatted("Torneo Sin Bracket")))
                    .when(bracketService).findByTournamentId("T001");

            assertThrows(BracketException.class, () -> controller.findByTournament("T001"));
        }

        @Test
        @DisplayName("EP-BC-05: advanceWinner() propaga BracketException si partido sin resultado")
        void advanceWinnerSinResultadoPropagaExcepcion() {
            doThrow(new BracketException("matchId", BracketException.RESULT_NOT_REGISTERED))
                    .when(bracketService).advanceWinner("T001", "M001");

            assertThrows(BracketException.class, () -> controller.advanceWinner("T001", "M001"));
        }

        @Test
        @DisplayName("EP-BC-06: advanceWinner() propaga BracketException si partido terminó en empate")
        void advanceWinnerEmpatePropagaExcepcion() {
            doThrow(new BracketException("match", BracketException.DRAW_NO_WINNER))
                    .when(bracketService).advanceWinner("T001", "M002");

            assertThrows(BracketException.class, () -> controller.advanceWinner("T001", "M002"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-BC-01: generate() retorna el body exacto del servicio")
        void generateRetornaBodyDelServicio() {
            GenerateBracketRequest req = new GenerateBracketRequest(4);
            BracketResponse esperado = buildResponse("T001", "Torneo Body");
            when(bracketService.generate("T001", req)).thenReturn(esperado);

            ResponseEntity<BracketResponse> response = controller.generate("T001", req);
            assertSame(esperado, response.getBody());
        }

        @Test
        @DisplayName("CS-BC-02: findByTournament() llama al servicio exactamente una vez")
        void findByTournamentLlamaServicioUnaVez() {
            when(bracketService.findByTournamentId("T-CHECK"))
                    .thenReturn(buildResponse("T-CHECK", "T"));

            controller.findByTournament("T-CHECK");
            verify(bracketService, times(1)).findByTournamentId("T-CHECK");
        }

        @Test
        @DisplayName("CS-BC-03: advanceWinner() llama al servicio con tournamentId y matchId correctos")
        void advanceWinnerLlamaServicio() {
            when(bracketService.advanceWinner("T001", "M001"))
                    .thenReturn(buildResponse("T001", "T"));

            controller.advanceWinner("T001", "M001");
            verify(bracketService, times(1)).advanceWinner("T001", "M001");
        }
    }

    // ── Helpers

    private BracketResponse buildResponse(String tournamentId, String name) {
        return new BracketResponse(tournamentId, name, List.of(
                new PhaseDTO("SEMI_FINALS", List.of())
        ));
    }
}
