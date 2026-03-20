package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    // CREATE
    void registrar(Player jugador, String correo);

    // UPDATE
    void actualizarPerfil(Player jugador, String foto);
    void cambiarDisponibilidad(Player jugador, boolean disponible);

    // READ
    List<Player> listarJugadores();
    Optional<Player> buscarPorId(String id);
    Player obtenerPorId(String id);          // lanza PlayerException si no existe

    // DELETE
    void eliminarJugador(String id);         // lanza PlayerException si no existe
}