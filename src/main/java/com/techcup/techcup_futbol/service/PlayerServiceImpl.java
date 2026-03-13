package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.DataStore;
import com.techcup.techcup_futbol.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    // CREATE
    @Override
    public void registrar(Player jugador, String correo) {
        if (correo.endsWith("@escuelaing.edu.co") || correo.endsWith("@gmail.com")) {
            jugador.setEmail(correo);
            DataStore.jugadores.put(jugador.getId(), jugador);
        } else {
            throw new IllegalArgumentException("Correo no válido");
        }
    }

    // UPDATE
    @Override
    public void actualizarPerfil(Player jugador, String foto) {
        jugador.setPhotoUrl(foto);
    }

    @Override
    public void cambiarDisponibilidad(Player jugador) {
        jugador.changeAvailability();
    }

    // READ
    @Override
    public List<Player> listarJugadores() {
        return new ArrayList<>(DataStore.jugadores.values());
    }

    @Override
    public Optional<Player> buscarPorId(String id) {
        return Optional.ofNullable(DataStore.jugadores.get(id));
    }

    // DELETE
    @Override
    public void eliminarJugador(String id) {
        DataStore.jugadores.remove(id);
    }
}