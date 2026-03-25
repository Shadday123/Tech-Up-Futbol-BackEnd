package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Standings {


    @Id
    private String id;

    @OneToOne
    private Team team;

    private int matchesPlayed;   // Partidos jugados
    private int matchesWon;      // Partidos ganados
    private int matchesDrawn;    // Partidos empatados
    private int matchesLost;     // Partidos perdidos
    private int goalsFor;        // Goles a favor
    private int goalsAgainst;    // Goles en contra
    private int goalsDifference; // Diferencia de gol
    private int points;          // Puntos totales

    public void calculateStatsFromMatch(List<Match> teamMatches) {
        this.matchesPlayed = teamMatches.size();

        this.goalsFor = teamMatches.stream()
                .mapToInt(match -> match.getGoalsFor(this.team))
                .sum();

        this.goalsAgainst = teamMatches.stream()
                .mapToInt(match -> match.getGoalsAgainst(this.team))
                .sum();

        long wins = teamMatches.stream()
                .filter(match -> match.getGoalsFor(this.team) > match.getGoalsAgainst(this.team))
                .count();

        long draws = teamMatches.stream()
                .filter(match -> match.getGoalsFor(this.team) == match.getGoalsAgainst(this.team))
                .count();

        this.points = (int) (wins * 3 + draws);

        this.matchesWon = (int) wins;
        this.matchesDrawn = (int) draws;
        this.matchesLost = this.matchesPlayed - (this.matchesWon + this.matchesDrawn);

        this.goalsDifference = this.goalsFor - this.goalsAgainst;
    }
}
