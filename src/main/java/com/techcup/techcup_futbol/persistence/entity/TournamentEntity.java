package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import com.techcup.techcup_futbol.core.model.*;
import lombok.Data;
import com.techcup.techcup_futbol.util.IdGenerator;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "tournaments")
public class TournamentEntity {

    @Id
    private String id;

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

    @ElementCollection
    @CollectionTable(name = "tournament_important_dates", joinColumns = @JoinColumn(name = "tournament_id"))
    @Column(name = "date_entry")
    private List<String> importantDates;

    @ElementCollection
    @CollectionTable(name = "tournament_match_schedules", joinColumns = @JoinColumn(name = "tournament_id"))
    @Column(name = "schedule_entry")
    private List<String> matchSchedules;

    @ElementCollection
    @CollectionTable(name = "tournament_fields", joinColumns = @JoinColumn(name = "tournament_id"))
    @Column(name = "field_entry")
    private List<String> fields;

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

    public List<MatchEntity> generateFixture(List<TeamEntity> equipos) {
        if (equipos == null || equipos.size() < 2) {
            throw new IllegalArgumentException(
                    "Se necesitan al menos 2 equipos para generar el fixture");
        }

        List<TeamEntity> mezclados = new ArrayList<>(equipos);
        Collections.shuffle(mezclados, new SecureRandom());

        List<MatchEntity> partidos = new ArrayList<>();
        for (int i = 0; i + 1 < mezclados.size(); i += 2) {
            MatchEntity partido = new MatchEntity();
            partido.setId(IdGenerator.generateId());
            partido.setLocalTeam(mezclados.get(i));
            partido.setVisitorTeam(mezclados.get(i + 1));
            partidos.add(partido);
        }
        return partidos;
    }
}