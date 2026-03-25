package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.PlayerController;
import com.techcup.techcup_futbol.Controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.Controller.dto.PlayerResponse;
import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.PlayerService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerController Tests")
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    private PlayerController controller;

    @BeforeEach
    void setUp() {
        controller = new PlayerController(playerService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PC-01: registrar() retorna 201 CREATED con el jugador registrado")
        void registrarRetorna201() {
            PlayerDTO dto = buildPlayerDTO("hp01@escuelaing.edu.co", "HP Jugador", "STUDENT");
            doNothing().when(playerService).registrar(any(Player.class), eq(dto.getEmail()));

            ResponseEntity<?> response = controller.registrar(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-PC-02: registrar() llama al servicio con jugador y correo correctos")
        void registrarLlamaServicio() {
            PlayerDTO dto = buildPlayerDTO("hp02@escuelaing.edu.co", "HP Jugador2", "STUDENT");
            doNothing().when(playerService).registrar(any(Player.class), eq(dto.getEmail()));

            controller.registrar(dto);

            verify(playerService, times(1)).registrar(any(Player.class), eq("hp02@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("HP-PC-03: listar() retorna 200 OK con lista de jugadores")
        void listarRetorna200() {
            when(playerService.listarJugadores()).thenReturn(List.of(buildStudent(), buildStudent()));

            ResponseEntity<List<PlayerResponse>> response = controller.listar();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());
        }

        @Test
        @DisplayName("HP-PC-04: buscarPorId() retorna 200 OK cuando jugador existe")
        void buscarPorIdRetorna200() {
            StudentPlayer jugador = buildStudent();
            when(playerService.buscarPorId(jugador.getId())).thenReturn(Optional.of(jugador));

            ResponseEntity<?> response = controller.buscarPorId(jugador.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-PC-05: buscarPorId() retorna 404 cuando jugador no existe")
        void buscarPorIdRetorna404() {
            when(playerService.buscarPorId("NO-EXISTE")).thenReturn(Optional.empty());

            ResponseEntity<?> response = controller.buscarPorId("NO-EXISTE");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-PC-06: eliminar() retorna 204 NO CONTENT")
        void eliminarRetorna204() {
            doNothing().when(playerService).eliminarJugador("J-001");

            ResponseEntity<Void> response = controller.eliminar("J-001");

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(playerService, times(1)).eliminarJugador("J-001");
        }

        @Test
        @DisplayName("HP-PC-07: actualizarPerfil() retorna 200 OK con jugador actualizado")
        void actualizarPerfilRetorna200() {
            StudentPlayer jugador = buildStudent();
            when(playerService.obtenerPorId(jugador.getId())).thenReturn(jugador);
            doNothing().when(playerService).actualizarPerfil(jugador, "foto.jpg");

            ResponseEntity<?> response = controller.actualizarPerfil(
                    jugador.getId(), Map.of("photoUrl", "foto.jpg"));

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("HP-PC-08: cambiarDisponibilidad() retorna 200 OK")
        void cambiarDisponibilidadRetorna200() {
            StudentPlayer jugador = buildStudent();
            when(playerService.obtenerPorId(jugador.getId())).thenReturn(jugador);
            doNothing().when(playerService).cambiarDisponibilidad(jugador, true);

            ResponseEntity<?> response = controller.cambiarDisponibilidad(jugador.getId(), true);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-PC-01: registrar() propaga PlayerException del servicio")
        void registrarPropagaExcepcion() {
            PlayerDTO dto = buildPlayerDTO("err@hotmail.com", "Err User", "STUDENT");
            doThrow(new PlayerException("email", PlayerException.EMAIL_INVALID_DOMAIN.formatted("err@hotmail.com")))
                    .when(playerService).registrar(any(Player.class), anyString());

            assertThrows(PlayerException.class, () -> controller.registrar(dto));
        }

        @Test
        @DisplayName("EP-PC-02: eliminar() propaga PlayerException si jugador no existe")
        void eliminarPropagaExcepcion() {
            doThrow(new PlayerException("id", PlayerException.PLAYER_NOT_FOUND.formatted("NO-ID")))
                    .when(playerService).eliminarJugador("NO-ID");

            assertThrows(PlayerException.class, () -> controller.eliminar("NO-ID"));
        }

        @Test
        @DisplayName("EP-PC-03: actualizarPerfil() propaga PlayerException si jugador no existe")
        void actualizarPerfilPropagaExcepcion() {
            doThrow(new PlayerException("id", PlayerException.PLAYER_NOT_FOUND.formatted("NO-ID")))
                    .when(playerService).obtenerPorId("NO-ID");

            assertThrows(PlayerException.class,
                    () -> controller.actualizarPerfil("NO-ID", Map.of("photoUrl", "foto.jpg")));
        }

        @Test
        @DisplayName("EP-PC-04: cambiarDisponibilidad() propaga PlayerException si ya disponible")
        void cambiarDisponibilidadPropagaExcepcion() {
            StudentPlayer jugador = buildStudent();
            when(playerService.obtenerPorId(jugador.getId())).thenReturn(jugador);
            doThrow(new PlayerException("availability",
                    PlayerException.PLAYER_ALREADY_AVAILABLE.formatted(jugador.getFullname())))
                    .when(playerService).cambiarDisponibilidad(jugador, true);

            assertThrows(PlayerException.class,
                    () -> controller.cambiarDisponibilidad(jugador.getId(), true));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-PC-01: listar() retorna lista vacía con 200 OK si no hay jugadores")
        void listarRetornaVacioConOk() {
            when(playerService.listarJugadores()).thenReturn(List.of());

            ResponseEntity<List<PlayerResponse>> response = controller.listar();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("CS-PC-02: registrar() genera UUID si id del DTO es null")
        void registrarGeneraUuidSiIdNull() {
            PlayerDTO dto = buildPlayerDTO("uuid@escuelaing.edu.co", "UUID Test", "STUDENT");
            dto.setId(null);
            doNothing().when(playerService).registrar(any(Player.class), anyString());

            controller.registrar(dto);

            assertNotNull(dto.getId());
        }

        @Test
        @DisplayName("CS-PC-03: registrar() genera UUID si id del DTO está en blanco")
        void registrarGeneraUuidSiIdBlanco() {
            PlayerDTO dto = buildPlayerDTO("blank@escuelaing.edu.co", "Blank Test", "STUDENT");
            dto.setId("   ");
            doNothing().when(playerService).registrar(any(Player.class), anyString());

            controller.registrar(dto);

            assertNotNull(dto.getId());
            assertFalse(dto.getId().isBlank());
        }

        @Test
        @DisplayName("CS-PC-04: buscarPorId() llama al servicio exactamente una vez")
        void buscarPorIdLlamaServicioUnaVez() {
            when(playerService.buscarPorId("J-XYZ")).thenReturn(Optional.empty());

            controller.buscarPorId("J-XYZ");

            verify(playerService, times(1)).buscarPorId("J-XYZ");
        }

        @Test
        @DisplayName("CS-PC-05: eliminar() llama exactamente una vez al servicio con el ID correcto")
        void eliminarLlamaServicioUnaVez() {
            doNothing().when(playerService).eliminarJugador("J-DELETE");

            controller.eliminar("J-DELETE");

            verify(playerService, times(1)).eliminarJugador("J-DELETE");
        }
    }

    // ── Helpers

    private PlayerDTO buildPlayerDTO(String email, String fullname, String type) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setFullname(fullname);
        dto.setEmail(email);
        dto.setPassword("Password1");
        dto.setNumberID(123456);
        dto.setPosition(PositionEnum.Midfielder);
        dto.setDorsalNumber(10);
        dto.setAge(20);
        dto.setGender("Masculino");
        dto.setPlayerType(type);
        dto.setSemester(3);
        return dto;
    }

    private StudentPlayer buildStudent() {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname("Test Student");
        p.setEmail("student@escuelaing.edu.co");
        p.setNumberID(999999);
        p.setAge(20);
        p.setGender("Masculino");
        p.setSemester(4);
        p.setDorsalNumber(5);
        p.setPosition(PositionEnum.Defender);
        p.setHaveTeam(false);
        return p;
    }
}
