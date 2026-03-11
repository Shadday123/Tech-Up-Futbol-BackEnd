package com.techcup.techcup_futbol.model;

import lombok.Data;

import java.util.List;

@Data
public class Player {
    private String nombre;
    private String correo;
    private String tipo;
    private List<Posicion> posiciones;
    private String dorsal;

    private String foto;
    private boolean disponible;
}
