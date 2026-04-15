package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.PlayerController;
import com.techcup.techcup_futbol.controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock PlayerService playerService;
    @InjectMocks PlayerController playerController;

    private StudentPlayer player;
    private PlayerDTO dto;

    @BeforeEach
    void setUp() {
        player = new StudentPlayer();
        player.setId("p1");
        player.setFullname("Juan Perez");
        player.setEmail("juan@example.com");
        player.setPosition(PositionEnum.Midfielder);
        player.setAge(22);
        player.setSemester(5);
        player.setDorsalNumber(10);

        dto = new PlayerDTO();
        dto.setFullname("Juan Perez");
        dto.setEmail("juan@example.com");
        dto.setPlayerType("STUDENT");
        dto.setSemester(5);
        dto.setPosition(PositionEnum.Midfielder);
        dto.setAge(22);
        dto.setDorsalNumber(10);
        dto.setGender("M");
        dto.setNumberID(123456);
    }

    @Test
    void registrar_validDto_returnsCreated() {
        doNothing().when(playerService).registrar(any(Player.class), anyString());

        ResponseEntity<?> response = playerController.registrar(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(playerService).registrar(any(Player.class), eq("juan@example.com"));
    }

    @Test
    void actualizarPerfil_returnsOk() {
        when(playerService.obtenerPorId("p1")).thenReturn(player);
        doNothing().when(playerService).actualizarPerfil(any(Player.class), anyString());

        ResponseEntity<?> response = playerController.actualizarPerfil(
                "p1", Map.of("photoUrl", "http://foto.com/new.jpg"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(playerService).actualizarPerfil(any(Player.class), eq("http://foto.com/new.jpg"));
    }

    @Test
    void cambiarDisponibilidad_returnsOk() {
        when(playerService.obtenerPorId("p1")).thenReturn(player);
        doNothing().when(playerService).cambiarDisponibilidad(any(Player.class), anyBoolean());

        ResponseEntity<?> response = playerController.cambiarDisponibilidad("p1", false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(playerService).cambiarDisponibilidad(any(Player.class), eq(false));
    }

    @Test
    void listar_returnsOkWithList() {
        when(playerService.listarJugadores()).thenReturn(List.of(player));

        ResponseEntity<?> response = playerController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(playerService).listarJugadores();
    }

    @Test
    void buscarPorId_existing_returnsOk() {
        when(playerService.buscarPorId("p1")).thenReturn(Optional.of(player));

        ResponseEntity<?> response = playerController.buscarPorId("p1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void buscarPorId_notFound_returnsNotFound() {
        when(playerService.buscarPorId("p999")).thenReturn(Optional.empty());

        ResponseEntity<?> response = playerController.buscarPorId("p999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminar_returnsNoContent() {
        doNothing().when(playerService).eliminarJugador("p1");

        ResponseEntity<Void> response = playerController.eliminar("p1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(playerService).eliminarJugador("p1");
    }
}
