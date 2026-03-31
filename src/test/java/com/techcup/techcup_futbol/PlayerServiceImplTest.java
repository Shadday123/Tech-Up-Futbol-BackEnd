package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.service.PlayerServiceImpl;
import com.techcup.techcup_futbol.core.validator.PlayerValidator;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerValidator playerValidator;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private StudentPlayer validPlayer;

    @BeforeEach
    void setUp() {
        validPlayer = new StudentPlayer();
        validPlayer.setFullname("Juan Perez");
        validPlayer.setAge(22);
        validPlayer.setPosition(PositionEnum.Midfielder);
        validPlayer.setNumberID(123456);
        validPlayer.setDorsalNumber(10);
        validPlayer.setDisponible(true);
        validPlayer.setSemester(5);
    }

    // ── REGISTRAR ──

    @Test
    void registrar_validPlayer_setsIdAndSaves() {
        String correo = "juan@gmail.com";
        doNothing().when(playerValidator).validate(any(), eq(correo));

        playerService.registrar(validPlayer, correo);

        assertNotNull(validPlayer.getId());
        assertEquals(correo, validPlayer.getEmail());
        verify(playerValidator).validate(validPlayer, correo);
        verify(playerRepository).save(validPlayer);
    }

    // ── ACTUALIZAR PERFIL ──

    @Test
    void actualizarPerfil_updatesPhotoUrl() {
        validPlayer.setId("p-001");
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(validPlayer));

        playerService.actualizarPerfil(validPlayer, "http://photo.com/new.jpg");

        assertEquals("http://photo.com/new.jpg", validPlayer.getPhotoUrl());
    }

    // ── CAMBIAR DISPONIBILIDAD ──

    @Test
    void cambiarDisponibilidad_toUnavailable_togglesState() {
        validPlayer.setId("p-001");
        validPlayer.setDisponible(true);
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(validPlayer));

        playerService.cambiarDisponibilidad(validPlayer, false);

        assertFalse(validPlayer.isDisponible());
    }

    @Test
    void cambiarDisponibilidad_sameState_throwsException() {
        validPlayer.setId("p-001");
        validPlayer.setDisponible(true);
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(validPlayer));

        assertThrows(PlayerException.class,
                () -> playerService.cambiarDisponibilidad(validPlayer, true));
    }

    // ── LISTAR JUGADORES ──

    @Test
    void listarJugadores_returnsList() {
        List<StudentPlayer> jugadores = List.of(validPlayer);
        when(playerRepository.findAll()).thenReturn(List.copyOf(jugadores));

        var result = playerService.listarJugadores();

        assertEquals(1, result.size());
        verify(playerRepository).findAll();
    }

    // ── BUSCAR POR ID ──

    @Test
    void buscarPorId_existing_returnsOptional() {
        validPlayer.setId("p-001");
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(validPlayer));

        Optional<?> result = playerService.buscarPorId("p-001");

        assertTrue(result.isPresent());
        assertEquals(validPlayer, result.get());
    }

    @Test
    void buscarPorId_nonExistent_returnsEmpty() {
        when(playerRepository.findById("p-999")).thenReturn(Optional.empty());

        Optional<?> result = playerService.buscarPorId("p-999");

        assertTrue(result.isEmpty());
    }

    // ── OBTENER POR ID ──

    @Test
    void obtenerPorId_nonExistent_throwsPlayerException() {
        when(playerRepository.findById("p-999")).thenReturn(Optional.empty());

        assertThrows(PlayerException.class,
                () -> playerService.obtenerPorId("p-999"));
    }

    // ── ELIMINAR JUGADOR ──

    @Test
    void eliminarJugador_existing_deletesPlayer() {
        validPlayer.setId("p-001");
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(validPlayer));

        playerService.eliminarJugador("p-001");

        verify(playerRepository).deleteById("p-001");
    }
}
