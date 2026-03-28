package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "matches")
public class Match {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "local_team_id")
    private Team localTeam;

    @ManyToOne
    @JoinColumn(name = "visitor_team_id")
    private Team visitorTeam;

    private LocalDateTime dateTime;

    private int scoreLocal;
    private int scoreVisitor;

    private int yellowCards;
    private int redCards;

    private int field;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.SCHEDULED;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Team winner;

    @ManyToOne
    @JoinColumn(name = "referee_id")
    private Referee referee;

    public int getGoalsFor(Team team) {
        if (team.equals(localTeam)) return scoreLocal;
        if (team.equals(visitorTeam)) return scoreVisitor;
        return 0;
    }

    public int getGoalsAgainst(Team team) {
        if (team.equals(localTeam)) return scoreVisitor;
        if (team.equals(visitorTeam)) return scoreLocal;
        return 0;
    }
}
