package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final List<Player> jugadores = new ArrayList<>();

    //CREATE
    @Override
    public void registrar(Player jugador, String correo) {

        if(correo.endsWith("@escuelaing.edu.co") || correo.endsWith("@gmail.com")){
            jugador.setEmail(correo);
            jugadores.add(jugador);
        }else{
            throw new IllegalArgumentException("Correo no válido");
        }

    }

    //UPDATE
    @Override
    public void actualizarPerfil(Player jugador, String foto) {
        jugador.setPhotoUrl(foto);
    }

    @Override
    public void cambiarDisponibilidad(Player jugador) {
        jugador.setHaveTeam(!jugador.isHaveTeam());
    }

    //READ
    @Override
    public List<Player> listarJugadores() {
        return jugadores;
    }

    @Override
    public Optional<Player> buscarPorId(String id) {
        return jugadores.stream()
                .filter(j -> j.getId().equals(id))
                .findFirst();
    }

    //DELETE
    @Override
    public void eliminarJugador(String id) {
        jugadores.removeIf(j -> j.getId().equals(id));
    }

}