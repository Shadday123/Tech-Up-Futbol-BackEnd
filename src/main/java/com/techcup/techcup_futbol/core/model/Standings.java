package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "standings")
public class Standings {

    @Id
    private String id;

    @Column(name = "tournament_id", nullable = false)
    private String tournamentId;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private int matchesPlayed;
    private int matchesWon;
    private int matchesDrawn;
    private int matchesLost;
    private int goalsFor;
    private int goalsAgainst;
    private int goalsDifference;
    private int points;

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
