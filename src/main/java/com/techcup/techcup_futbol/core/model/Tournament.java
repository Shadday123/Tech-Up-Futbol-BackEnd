package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;

import com.techcup.techcup_futbol.util.IdGenerator;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name= "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private Double registrationFee;

    private int maxTeams;

    private String configId;

    private String rules;

    private LocalDateTime registrationDeadline;

    private List<String> importantDates;   // formato: "descripcion|datetime"

    private List<String> matchSchedules;   // formato: "dia|horaInicio|horaFin"

    private List<String> fields;           // formato: "nombre|ubicacion"

    private String sanctions;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentState currentState;

    public void startTournament() {
        this.currentState = TournamentState.IN_PROGRESS;
    }

    public void finalizeTournament() {
        this.currentState = TournamentState.COMPLETED;
    }

    public boolean hasConfig() {
        return this.configId != null;
    }

    public List<Match> generateFixture(List<Team> equipos) {
        if (equipos == null || equipos.size() < 2) {
            throw new IllegalArgumentException(
                    "Se necesitan al menos 2 equipos para generar el fixture");
        }

        List<Team> mezclados = new ArrayList<>(equipos);
        Collections.shuffle(mezclados, new SecureRandom());

        List<Match> partidos = new ArrayList<>();
        for (int i = 0; i + 1 < mezclados.size(); i += 2) {
            Match partido = new Match();
            partido.setId(IdGenerator.generateId());
            partido.setLocalTeam(mezclados.get(i));
            partido.setVisitorTeam(mezclados.get(i + 1));
            partidos.add(partido);
        }
        return partidos;
    }
}
