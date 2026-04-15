package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.service.PlayerServiceImpl;
import com.techcup.techcup_futbol.core.validator.PlayerValidator;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity;
import com.techcup.techcup_futbol.persistence.mapper.PlayerPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PlayerValidator playerValidator;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private StudentPlayerEntity studentPlayerEntity;
    private PlayerEntity playerEntity;
    private StudentPlayer studentPlayer;

    @BeforeEach
    void setUp() {
        studentPlayer = new StudentPlayer();
        studentPlayer.setFullname("Juan Perez");
        studentPlayer.setAge(22);
        studentPlayer.setPosition(PositionEnum.Midfielder);
        studentPlayer.setNumberID(123456);
        studentPlayer.setDorsalNumber(10);
        studentPlayer.setDisponible(true);
        studentPlayer.setSemester(5);

        playerEntity = PlayerPersistenceMapper.toEntity(studentPlayer);
        studentPlayerEntity = (StudentPlayerEntity) playerEntity;
    }

    // ── REGISTRAR ──

    @Test
    void registrar_validPlayer_setsIdAndSaves() {
        String correo = "juan@gmail.com";
        when(playerRepository.existsByEmailIgnoreCase(correo)).thenReturn(false);
        doNothing().when(playerValidator).validate(studentPlayer, correo);

        playerService.registrar(studentPlayer, correo);

        assertNotNull(studentPlayer.getId());
        assertEquals(correo, studentPlayer.getEmail());
        verify(playerValidator).validate(studentPlayer, correo);
        verify(playerRepository).save(any(PlayerEntity.class));
    }

    @Test
    void registrar_emailAlreadyExists_throwsException() {
        String correo = "juan@gmail.com";
        when(playerRepository.existsByEmailIgnoreCase(correo)).thenReturn(true);

        PlayerException exception = assertThrows(PlayerException.class,
                () -> playerService.registrar(studentPlayer, "juan@gmail.com"));

        assertEquals("email", exception.getField());
        verify(playerValidator, never()).validate(any(), anyString());
    }

    // ── ACTUALIZAR PERFIL ──

    @Test
    void actualizarPerfil_updatesPhotoUrl() {
        studentPlayer.setId("p-001");
        playerEntity.setId("p-001");
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(playerEntity));
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity);

        playerService.actualizarPerfil(studentPlayer, "http://photo.com/new.jpg");

        verify(playerRepository).save(argThat(entity ->
                "http://photo.com/new.jpg".equals(entity.getPhotoUrl())));
    }

    // ── CAMBIAR DISPONIBILIDAD ──

    @Test
    void cambiarDisponibilidad_toUnavailable_togglesState() {
        studentPlayer.setId("p-001");
        playerEntity.setId("p-001");
        playerEntity.setDisponible(true);
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(playerEntity));
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity);

        playerService.cambiarDisponibilidad(studentPlayer, false);

        verify(playerRepository).save(argThat(entity -> !entity.isDisponible()));
    }

    @Test
    void cambiarDisponibilidad_sameState_throwsException() {
        studentPlayer.setId("p-001");
        playerEntity.setId("p-001");
        playerEntity.setDisponible(true);
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(playerEntity));

        PlayerException exception = assertThrows(PlayerException.class,
                () -> playerService.cambiarDisponibilidad(studentPlayer, true));

        assertEquals("availability", exception.getField());
    }

    // ── LISTAR JUGADORES ──

    @Test
    void listarJugadores_returnsList() {
        List<PlayerEntity> entities = List.of(playerEntity);
        when(playerRepository.findAll()).thenReturn(entities);

        var result = playerService.listarJugadores();

        assertEquals(1, result.size());
        assertEquals("Juan Perez", result.get(0).getFullname());
        verify(playerRepository).findAll();
    }

    // ── NUEVOS MÉTODOS DEL REPOSITORIO ──

    @Test
    void listarJugadoresSinEquipo_returnsList() {
        List<PlayerEntity> sinEquipo = List.of(playerEntity);
        when(playerRepository.findByHaveTeamFalse()).thenReturn(sinEquipo);

        var result = playerService.listarJugadoresSinEquipo();

        assertEquals(1, result.size());
        verify(playerRepository).findByHaveTeamFalse();
    }

    @Test
    void listarPorPosicion_returnsList() {
        List<PlayerEntity> midfielders = List.of(playerEntity);
        when(playerRepository.findByPosition(PositionEnum.Midfielder)).thenReturn(midfielders);

        var result = playerService.listarPorPosicion(PositionEnum.Midfielder);

        assertEquals(1, result.size());
        verify(playerRepository).findByPosition(PositionEnum.Midfielder);
    }

    @Test
    void buscarPorEmail_returnsList() {
        List<PlayerEntity> coincidencias = List.of(playerEntity);
        when(playerRepository.findByEmailContaining("perez")).thenReturn(coincidencias);

        var result = playerService.buscarPorEmail("perez");

        assertEquals(1, result.size());
        verify(playerRepository).findByEmailContaining("perez");
    }

    @Test
    void listarEstudiantesPorSemestre_returnsList() {
        List<StudentPlayerEntity> semestre5 = List.of(studentPlayerEntity);
        when(playerRepository.findBySemester(5)).thenReturn(semestre5);

        var result = playerService.listarEstudiantesPorSemestre(5);

        assertEquals(1, result.size());
        verify(playerRepository).findBySemester(5);
    }

    // ── BUSCAR POR ID ──

    @Test
    void buscarPorId_existing_returnsOptional() {
        studentPlayer.setId("p-001");
        playerEntity.setId("p-001");
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(playerEntity));

        Optional<Player> result = playerService.buscarPorId("p-001");

        assertTrue(result.isPresent());
        assertEquals("Juan Perez", result.get().getFullname());
    }

    @Test
    void buscarPorId_nonExistent_returnsEmpty() {
        when(playerRepository.findById("p-999")).thenReturn(Optional.empty());

        Optional<Player> result = playerService.buscarPorId("p-999");

        assertTrue(result.isEmpty());
    }

    // ── OBTENER POR ID ──

    @Test
    void obtenerPorId_nonExistent_throwsPlayerException() {
        when(playerRepository.findById("p-999")).thenReturn(Optional.empty());

        PlayerException exception = assertThrows(PlayerException.class,
                () -> playerService.obtenerPorId("p-999"));

        assertEquals("id", exception.getField());
    }

    // ── ELIMINAR JUGADOR ──

    @Test
    void eliminarJugador_existing_deletesPlayer() {
        studentPlayer.setId("p-001");
        playerEntity.setId("p-001");
        when(playerRepository.findById("p-001")).thenReturn(Optional.of(playerEntity));

        playerService.eliminarJugador("p-001");

        verify(playerRepository).deleteById("p-001");
    }
}
