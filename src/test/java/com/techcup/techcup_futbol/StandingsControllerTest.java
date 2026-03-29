package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.StandingsController;
import com.techcup.techcup_futbol.Controller.dto.StandingsResponse;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.StandingsService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StandingsController Tests")
class StandingsControllerTest {

    @Mock
    private StandingsService standingsService;

    private StandingsController controller;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        controller = new StandingsController(standingsService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-SC-01: findByTournament() retorna 200 OK con la tabla de posiciones")
        void findByTournamentRetorna200() {
            StandingsResponse resp = new StandingsResponse("T001", "Torneo Test", List.of());
            when(standingsService.findByTournamentId("T001")).thenReturn(resp);

            ResponseEntity<StandingsResponse> response = controller.findByTournament("T001");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("T001", response.getBody().tournamentId());
        }

        @Test
        @DisplayName("HP-SC-02: registerTeam() retorna 200 OK cuando equipo y torneo existen")
        void registerTeamRetorna200() {
            Team equipo = buildEquipo("Equipo Reg");
            DataStore.equipos.put(equipo.getId(), equipo);
            doNothing().when(standingsService).registerTeamInTournament("T001", equipo);

            ResponseEntity<String> response = controller.registerTeam("T001", equipo.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("Equipo Reg"));
        }

        @Test
        @DisplayName("HP-SC-03: registerTeam() llama al servicio con los parámetros correctos")
        void registerTeamLlamaServicio() {
            Team equipo = buildEquipo("Equipo Call");
            DataStore.equipos.put(equipo.getId(), equipo);
            doNothing().when(standingsService).registerTeamInTournament(anyString(), eq(equipo));

            controller.registerTeam("T002", equipo.getId());

            verify(standingsService, times(1)).registerTeamInTournament("T002", equipo);
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-SC-01: registerTeam() retorna 400 BAD REQUEST si equipo no existe en DataStore")
        void registerTeamEquipoNoExisteRetorna400() {
            ResponseEntity<String> response = controller.registerTeam("T001", "NO-EXISTE");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().contains("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-SC-02: findByTournament() propaga TournamentException si torneo no existe")
        void findByTournamentPropagaExcepcion() {
            doThrow(new TournamentException("id",
                    TournamentException.TOURNAMENT_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(standingsService).findByTournamentId("NO-EXISTE");

            assertThrows(TournamentException.class,
                    () -> controller.findByTournament("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-SC-03: registerTeam() no llama al servicio si equipo no existe")
        void registerTeamNoLlamaServicioSiEquipoNoExiste() {
            controller.registerTeam("T001", "NO-EXISTE");
            verify(standingsService, never()).registerTeamInTournament(anyString(), any(Team.class));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-SC-01: findByTournament() llama al servicio exactamente una vez")
        void findByTournamentLlamaServicioUnaVez() {
            when(standingsService.findByTournamentId("T-CHECK"))
                    .thenReturn(new StandingsResponse("T-CHECK", "T", List.of()));

            controller.findByTournament("T-CHECK");
            verify(standingsService, times(1)).findByTournamentId("T-CHECK");
        }

        @Test
        @DisplayName("CS-SC-02: registerTeam() mensaje de éxito contiene el nombre del equipo")
        void registerTeamMensajeContieneNombre() {
            Team equipo = buildEquipo("Mensaje Equipo");
            DataStore.equipos.put(equipo.getId(), equipo);
            doNothing().when(standingsService).registerTeamInTournament(anyString(), eq(equipo));

            ResponseEntity<String> response = controller.registerTeam("T001", equipo.getId());

            assertTrue(response.getBody().contains("Mensaje Equipo"));
        }

        @Test
        @DisplayName("CS-SC-03: múltiples equipos distintos pueden registrarse en el mismo torneo")
        void multiplesEquiposRegistradosEnMismoTorneo() {
            Team e1 = buildEquipo("Equipo Multi 1");
            Team e2 = buildEquipo("Equipo Multi 2");
            DataStore.equipos.put(e1.getId(), e1);
            DataStore.equipos.put(e2.getId(), e2);
            doNothing().when(standingsService).registerTeamInTournament(anyString(), any(Team.class));

            ResponseEntity<String> r1 = controller.registerTeam("T001", e1.getId());
            ResponseEntity<String> r2 = controller.registerTeam("T001", e2.getId());

            assertEquals(HttpStatus.OK, r1.getStatusCode());
            assertEquals(HttpStatus.OK, r2.getStatusCode());
            verify(standingsService, times(2)).registerTeamInTournament(eq("T001"), any(Team.class));
        }
    }

    // ── Helpers

    private Team buildEquipo(String name) {
        Team team = new Team();
        team.setId(UUID.randomUUID().toString());
        team.setTeamName(name);
        team.setShieldUrl("shield.png");
        team.setUniformColors(Collections.singletonList("Azul"));
        team.setPlayers(new ArrayList<>());
        return team;
    }
}
