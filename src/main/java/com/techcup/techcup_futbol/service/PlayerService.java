package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Player;

public interface PlayerService {
    default void registrar(Player jugador, String correo){
        if (correo.endsWith("@escuelaing.edu.co")){

        }
    }
    default void actualizarPerfil(Player jugador, String foto){
        jugador.setFoto(foto);
    }

    default void cambiarDisponibilidad(Player jugador){
        boolean disponibilidad = jugador.isDisponible();
        if(disponibilidad){
            jugador.setDisponible(false);
        }
        else{
            jugador.setDisponible(true);
        }
    }
}
