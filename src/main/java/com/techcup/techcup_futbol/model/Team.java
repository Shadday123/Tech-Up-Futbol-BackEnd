package com.techcup.techcup_futbol.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String escudo;
    private String coloresUniforme;
    @ManyToOne
    private Player capitan;
    @OneToMany
    private List<Player> jugadores = new ArrayList<>();
    private boolean activo;
    private String fotoEquipo;


    public Team(String nombre, String coloresUniforme, String fotoEquipo) {
        this.nombre = nombre;
        this.coloresUniforme = coloresUniforme;
        this.fotoEquipo = fotoEquipo;
        this.activo = true;
    }

    public Team() {

    }

    public void agregarJugador(Player jugador) {
        if (jugadores != null && !jugadores.contains(jugador)) {
            jugadores.add(jugador);
        }
    }

    public void eliminarJugador(Player jugador) {
        if (jugadores != null) {
            jugadores.remove(jugador);
        }
    }

    public boolean tieneJugador(Player jugador) {
        return jugadores != null && jugadores.contains(jugador);
    }

    public int getCantidadJugadores() {
        return jugadores != null ? jugadores.size() : 0;
    }
}
