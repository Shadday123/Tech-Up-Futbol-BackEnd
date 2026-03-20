package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.PlayerSearchController;
import com.techcup.techcup_futbol.Controller.dto.PlayerSearchRequest;
import com.techcup.techcup_futbol.Controller.dto.PlayerSearchResult;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.service.PlayerSearchService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerSearchController Tests")
class PlayerSearchControllerTest {

    @Mock
    private PlayerSearchService playerSearchService;

    private PlayerSearchController controller;

    @BeforeEach
    void setUp() {
        controller = new PlayerSearchController(playerSearchService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PSC-01: search() sin filtros retorna 200 OK con todos los disponibles")
        void searchSinFiltrosRetorna200() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of(buildResult("Carlos"), buildResult("Juan")));

            ResponseEntity<List<PlayerSearchResult>> response =
                    controller.search(null, null, null, null, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }

        @Test
        @DisplayName("HP-PSC-02: search() con posición retorna 200 OK filtrando por posición")
        void searchConPosicionRetorna200() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of(buildResult("Portero")));

            ResponseEntity<List<PlayerSearchResult>> response =
                    controller.search(PositionEnum.GoalKeeper, null, null, null, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
        }

        @Test
        @DisplayName("HP-PSC-03: search() con semestre retorna 200 OK filtrando por semestre")
        void searchConSemestreRetorna200() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of(buildResult("Semestre 4")));

            ResponseEntity<List<PlayerSearchResult>> response =
                    controller.search(null, 4, null, null, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-PSC-04: search() con rango de edad retorna 200 OK")
        void searchConRangoEdadRetorna200() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of(buildResult("Edad 22")));

            ResponseEntity<List<PlayerSearchResult>> response =
                    controller.search(null, null, 20, 25, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-PSC-05: search() con nombre retorna 200 OK filtrando por nombre")
        void searchConNombreRetorna200() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of(buildResult("Carlos Match")));

            ResponseEntity<List<PlayerSearchResult>> response =
                    controller.search(null, null, null, null, null, "carlos", null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-PSC-06: search() retorna lista vacía con 200 OK si nadie coincide")
        void searchRetornaVacioConOk() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of());

            ResponseEntity<List<PlayerSearchResult>> response =
                    controller.search(PositionEnum.GoalKeeper, null, null, null, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-PSC-01: search() propaga cualquier excepción del servicio")
        void searchPropagaExcepcion() {
            doThrow(new RuntimeException("Error interno"))
                    .when(playerSearchService).search(any(PlayerSearchRequest.class));

            assertThrows(RuntimeException.class,
                    () -> controller.search(null, null, null, null, null, null, null));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-PSC-01: search() construye PlayerSearchRequest con todos los filtros correctos")
        void searchConstruyeRequestConFiltros() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of());

            controller.search(PositionEnum.Defender, 3, 18, 25, "Masculino", "carlos", 12345);

            verify(playerSearchService, times(1)).search(argThat(req ->
                    req.position() == PositionEnum.Defender
                            && req.semester() == 3
                            && req.minAge() == 18
                            && req.maxAge() == 25
                            && "Masculino".equals(req.gender())
                            && "carlos".equals(req.name())
                            && req.numberID() == 12345
            ));
        }

        @Test
        @DisplayName("CS-PSC-02: search() sin filtros construye PlayerSearchRequest con todos null")
        void searchSinFiltrosConstruyeRequestVacio() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of());

            controller.search(null, null, null, null, null, null, null);

            verify(playerSearchService, times(1)).search(argThat(req ->
                    req.position() == null
                            && req.semester() == null
                            && req.minAge() == null
                            && req.maxAge() == null
                            && req.gender() == null
                            && req.name() == null
                            && req.numberID() == null
            ));
        }

        @Test
        @DisplayName("CS-PSC-03: search() llama al servicio exactamente una vez por request")
        void searchLlamaServicioUnaVez() {
            when(playerSearchService.search(any(PlayerSearchRequest.class)))
                    .thenReturn(List.of());

            controller.search(null, null, null, null, "Femenino", null, null);

            verify(playerSearchService, times(1)).search(any(PlayerSearchRequest.class));
        }

        @Test
        @DisplayName("CS-PSC-04: search() retorna el body exacto que devuelve el servicio")
        void searchRetornaBodyDelServicio() {
            List<PlayerSearchResult> esperado = List.of(buildResult("Exacto"));
            when(playerSearchService.search(any(PlayerSearchRequest.class))).thenReturn(esperado);

            ResponseEntity<List<PlayerSearchResult>> response =
                    controller.search(null, null, null, null, null, null, null);

            assertSame(esperado, response.getBody());
        }
    }

    // ── Helpers

    private PlayerSearchResult buildResult(String nombre) {
        return new PlayerSearchResult(
                UUID.randomUUID().toString(),
                nombre,
                PositionEnum.Midfielder,
                10,
                null,
                "STUDENT",
                4,
                22,
                "Masculino",
                true
        );
    }
}
