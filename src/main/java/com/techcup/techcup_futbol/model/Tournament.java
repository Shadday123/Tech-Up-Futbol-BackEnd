package com.techcup.techcup_futbol.model;

import lombok.Data;

import java.util.Date;


@Data
public class Tournament {
    private Date fechainicial;
    private Date fechaFinal;
    private int cantEquipos;
    private double costoPorEquipo;
    private StatusTournament estado;

}
