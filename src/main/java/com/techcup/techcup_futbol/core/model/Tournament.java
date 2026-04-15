package com.techcup.techcup_futbol.core.model;

import lombok.Data;
import com.techcup.techcup_futbol.core.util.IdGenerator;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Tournament {

    private String id;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Double registrationFee;

    private int maxTeams;

    private String configId;

    private String rules;

    private LocalDateTime registrationDeadline;

    private List<String> importantDates;

    private List<String> matchSchedules;

    private List<String> fields;

    private String sanctions;

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
