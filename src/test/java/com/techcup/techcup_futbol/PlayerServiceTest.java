package com.techcup.techcup_futbol;
import org.junit.jupiter.api.Test;

public class PlayerServiceTest {

    PlayerService service = new PlayerService() {};

    //Happy Path

    @Test
    void registrarJugadorCorreoValido() {

        Player jugador = new Player();
        jugador.setId(1L);
        jugador.setNombre("Juan");

        service.registrar(jugador,"juan@gmail.com");

        assertTrue(service.listarJugadores().contains(jugador));
    }

    //Happy Path

    @Test
    void actualizarPerfilJugador() {

        Player jugador = new Player();
        jugador.setFoto("foto1");

        service.actualizarPerfil(jugador,"foto2");

        assertEquals("foto2", jugador.getFoto());
    }

    //Happy Path

    @Test
    void cambiarDisponibilidad() {

        Player jugador = new Player();
        jugador.setDisponible(true);

        service.cambiarDisponibilidad(jugador);

        assertFalse(jugador.isDisponible());
    }
}
