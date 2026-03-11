package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PlayerService {

    List<Player> jugadores = new ArrayList<>();


    //CREATE
    default void registrar(Player jugador, String correo){

        if(correo.endsWith("@escuelaing.edu.co") || correo.endsWith("@gmail.com")){
            jugadores.add(jugador);
        }else{
            throw new IllegalArgumentException("Correo no válido");
        }

    }

    default void actualizarPerfil(Player jugador, String foto){
        jugador.setFoto(foto);
    }

    //UPDATE
    default void cambiarDisponibilidad(Player jugador){
        jugador.setDisponible(!jugador.isDisponible());
    }

    default List<Player> listarJugadores(){
        return jugadores;
    }

    //READ
    default Optional<Player> buscarPorId(Long id){
        return jugadores.stream()
                .filter(j -> j.getId().equals(id))
                .findFirst();
    }

    //DELETE
    default void eliminarJugador(Long id){
        jugadores.removeIf(j -> j.getId().equals(id));
    }

}