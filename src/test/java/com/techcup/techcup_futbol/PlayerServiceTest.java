package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.service.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceTest {

    private PlayerServiceImpl playerService;
    private Player jugadorTest;
    private String idTest;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos(); // evita que datos de un test se filtren al siguiente
        playerService = new PlayerServiceImpl();

        jugadorTest = new StudentPlayer();
        idTest = UUID.randomUUID().toString();
        jugadorTest.setId(idTest);
        jugadorTest.setFullname("Jugador Test");
        jugadorTest.setEmail("test@escuelaing.edu.co");
        jugadorTest.setNumberID(999999);
        jugadorTest.setPosition(PositionEnum.Midfielder);
        jugadorTest.setDorsalNumber(10);
        jugadorTest.setPhotoUrl("test.jpg");
        jugadorTest.setHaveTeam(false);
        jugadorTest.setAge(20);
        jugadorTest.setGender("Masculino");
        jugadorTest.setCaptain(false);
    }

    // HAPPY PATH TESTS

    @Test
    void testRegistrar_CorreoInstitucional_Exitoso() {
        String correo = "test@escuelaing.edu.co";

        playerService.registrar(jugadorTest, correo);

        List<Player> jugadores = playerService.listarJugadores();
        assertEquals(1, jugadores.size());
        assertEquals(correo, jugadores.get(0).getEmail());
    }

    @Test
    void testRegistrar_CorreoGmail_Exitoso() {
        String correo = "test@gmail.com";

        playerService.registrar(jugadorTest, correo);

        List<Player> jugadores = playerService.listarJugadores();
        assertEquals(1, jugadores.size());
        assertEquals(correo, jugadores.get(0).getEmail());
    }

    @Test
    void testActualizarPerfil_NuevaFoto_ActualizaExitosamente() {
        String nuevaFoto = "nueva_foto.jpg";

        playerService.actualizarPerfil(jugadorTest, nuevaFoto);

        assertEquals(nuevaFoto, jugadorTest.getPhotoUrl());
    }

    @Test
    void testCambiarDisponibilidad_Alternar_Exitoso() {
        boolean estadoInicial = jugadorTest.isHaveTeam();

        playerService.cambiarDisponibilidad(jugadorTest);
        boolean estadoPrimerCambio = jugadorTest.isHaveTeam();

        playerService.cambiarDisponibilidad(jugadorTest);
        boolean estadoSegundoCambio = jugadorTest.isHaveTeam();

        assertNotEquals(estadoInicial, estadoPrimerCambio);
        assertEquals(estadoInicial, estadoSegundoCambio);
    }

    @Test
    void testListarJugadores_ConRegistros_RetornaLista() {
        playerService.registrar(jugadorTest, "test@escuelaing.edu.co");

        List<Player> resultado = playerService.listarJugadores();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testBuscarPorId_JugadorExiste_RetornaJugador() {
        playerService.registrar(jugadorTest, "test@escuelaing.edu.co");

        Optional<Player> resultado = playerService.buscarPorId(idTest);

        assertTrue(resultado.isPresent());
        assertEquals(jugadorTest.getFullname(), resultado.get().getFullname());
    }

    @Test
    void testEliminarJugador_JugadorExiste_EliminaCorrectamente() {
        playerService.registrar(jugadorTest, "test@escuelaing.edu.co");

        playerService.eliminarJugador(idTest);

        assertEquals(0, playerService.listarJugadores().size());
        assertFalse(playerService.buscarPorId(idTest).isPresent());
    }

    // ERROR PATH TESTS

    @Test
    void testRegistrar_CorreoInvalido_LanzaExcepcion() {
        String correoInvalido = "test@hotmail.com";

        assertThrows(IllegalArgumentException.class, () -> {
            playerService.registrar(jugadorTest, correoInvalido);
        });
        assertEquals(0, playerService.listarJugadores().size());
    }

    @Test
    void testRegistrar_CorreoNulo_LanzaExcepcion() {
        assertThrows(Exception.class, () -> {
            playerService.registrar(jugadorTest, null);
        });
    }

    @Test
    void testRegistrar_CorreoVacio_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.registrar(jugadorTest, "");
        });
    }

    @Test
    void testRegistrar_JugadorNulo_LanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            playerService.registrar(null, "test@escuelaing.edu.co");
        });
    }

    @Test
    void testActualizarPerfil_JugadorNulo_LanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            playerService.actualizarPerfil(null, "foto.jpg");
        });
    }

    @Test
    void testCambiarDisponibilidad_JugadorNulo_LanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            playerService.cambiarDisponibilidad(null);
        });
    }

    @Test
    void testBuscarPorId_IdNoExiste_RetornaEmpty() {
        Optional<Player> resultado = playerService.buscarPorId("ID_INEXISTENTE");
        assertFalse(resultado.isPresent());
    }

    @Test
    void testEliminarJugador_IdNulo_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            playerService.eliminarJugador(null);
        });
    }

    @Test
    void testEliminarJugador_IdNoExiste_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            playerService.eliminarJugador("ID_INEXISTENTE");
        });
    }

    // CONDITIONAL TESTS

    @Test
    void testMultiplesRegistros_ManejaCorrectamente() {
        Player jugador2 = new StudentPlayer();
        jugador2.setId(UUID.randomUUID().toString());
        jugador2.setFullname("Jugador 2");
        jugador2.setEmail("jugador2@escuelaing.edu.co");

        playerService.registrar(jugadorTest, "test@escuelaing.edu.co");
        playerService.registrar(jugador2, "jugador2@gmail.com");

        List<Player> lista = playerService.listarJugadores();
        assertEquals(2, lista.size());
        assertTrue(lista.stream().anyMatch(p -> p.getEmail().contains("@escuelaing.edu.co")));
        assertTrue(lista.stream().anyMatch(p -> p.getEmail().contains("@gmail.com")));
    }

    @Test
    void testCambiarDisponibilidad_MultiplesVeces_AlternaCorrectamente() {
        boolean[] estados = new boolean[5];
        estados[0] = jugadorTest.isHaveTeam();

        for (int i = 1; i <= 4; i++) {
            playerService.cambiarDisponibilidad(jugadorTest);
            estados[i] = jugadorTest.isHaveTeam();
        }

        assertNotEquals(estados[0], estados[1]);
        assertEquals(estados[0], estados[2]);
        assertNotEquals(estados[0], estados[3]);
        assertEquals(estados[0], estados[4]);
    }

    @Test
    void testBuscarPorId_DespuesDeEliminar_NoEncuentra() {
        playerService.registrar(jugadorTest, "test@escuelaing.edu.co");
        playerService.eliminarJugador(idTest);

        Optional<Player> resultado = playerService.buscarPorId(idTest);

        assertFalse(resultado.isPresent());
    }

    @Test
    void testRegistrar_CorreoDuplicado_PermiteRegistro() {
        Player jugadorDuplicado = new StudentPlayer();
        jugadorDuplicado.setId(UUID.randomUUID().toString());
        jugadorDuplicado.setFullname("Jugador Duplicado");
        jugadorDuplicado.setEmail("test@escuelaing.edu.co");

        playerService.registrar(jugadorTest, "test@escuelaing.edu.co");
        playerService.registrar(jugadorDuplicado, "test@escuelaing.edu.co");

        assertEquals(2, playerService.listarJugadores().size());
    }

    @Test
    void testRegistrar_DorsalesDuplicados_PermiteRegistro() {
        Player jugador1 = new StudentPlayer();
        jugador1.setId(UUID.randomUUID().toString());
        jugador1.setFullname("Jugador 1");
        jugador1.setEmail("jugador1@escuelaing.edu.co");
        jugador1.setDorsalNumber(10);

        Player jugador2 = new StudentPlayer();
        jugador2.setId(UUID.randomUUID().toString());
        jugador2.setFullname("Jugador 2");
        jugador2.setEmail("jugador2@gmail.com");
        jugador2.setDorsalNumber(10);

        playerService.registrar(jugador1, "jugador1@escuelaing.edu.co");
        playerService.registrar(jugador2, "jugador2@gmail.com");

        assertEquals(2, playerService.listarJugadores().size());
    }
}