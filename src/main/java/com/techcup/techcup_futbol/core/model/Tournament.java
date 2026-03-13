package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Double registrationFee;

    private int maxTeams;

    private String rules;

    @Enumerated(EnumType.STRING)
    private TournamentState currentState;

    public void startTournament() {
        this.currentState = TournamentState.IN_PROGRESS;
    }

    public void finalizeTournament() {
        this.currentState = TournamentState.COMPLETED;
    }

    public void generateFixture() {}

}