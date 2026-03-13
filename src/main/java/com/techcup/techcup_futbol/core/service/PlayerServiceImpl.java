package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    // CREATE
    @Override
    public void registrar(Player jugador, String correo) {
        log.info("Intentando registrar jugador con correo: {}", correo);

        if (correo.endsWith("@escuelaing.edu.co") || correo.endsWith("@gmail.com")) {
            jugador.setEmail(correo);
            DataStore.jugadores.put(jugador.getId(), jugador);
            log.info("Jugador registrado exitosamente con id: {}", jugador.getId());
        } else {
            log.warn("Correo no válido: {}", correo);
            throw new IllegalArgumentException("Correo no válido");
        }
    }

    // UPDATE
    @Override
    public void actualizarPerfil(Player jugador, String foto) {
        log.info("Actualizando foto del jugador id: {}", jugador.getId());
        jugador.setPhotoUrl(foto);
    }

    @Override
    public void cambiarDisponibilidad(Player jugador) {
        log.info("Cambiando disponibilidad del jugador id: {} de {} a {}",
                jugador.getId(), jugador.isHaveTeam(), !jugador.isHaveTeam());
        jugador.changeAvailability();
    }

    // READ
    @Override
    public List<Player> listarJugadores() {
        List<Player> jugadores = new ArrayList<>(DataStore.jugadores.values());
        log.info("Listando jugadores — total: {}", jugadores.size());
        return jugadores;
    }

    @Override
    public Optional<Player> buscarPorId(String id) {
        log.info("Buscando jugador con id: {}", id);
        Optional<Player> resultado = Optional.ofNullable(DataStore.jugadores.get(id));

        if (resultado.isEmpty()) {
            log.warn("No se encontró jugador con id: {}", id);
        }

        return resultado;
    }

    // DELETE
    @Override
    public void eliminarJugador(String id) {
        log.info("Eliminando jugador con id: {}", id);
        DataStore.jugadores.remove(id);
        log.info("Jugador con id: {} eliminado del DataStore", id);
    }
}