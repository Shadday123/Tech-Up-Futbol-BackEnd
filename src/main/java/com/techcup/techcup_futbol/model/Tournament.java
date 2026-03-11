package com.techcup.techcup_futbol.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;


@Data
@Entity
public class Tournament {
    @Id
    private Long id;
    private Date fechainicial;
    private Date fechaFinal;
    private int cantEquipos;
    private double costoPorEquipo;
    private StatusTournament estado;

}
