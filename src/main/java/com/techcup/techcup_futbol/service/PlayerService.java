package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    void registrar(Player jugador, String correo);

    void actualizarPerfil(Player jugador, String foto);

    void cambiarDisponibilidad(Player jugador);

    List<Player> listarJugadores();

    Optional<Player> buscarPorId(String id);

    void eliminarJugador(String id);
}