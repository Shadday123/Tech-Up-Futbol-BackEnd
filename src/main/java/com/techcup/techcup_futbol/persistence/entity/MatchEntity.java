package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import com.techcup.techcup_futbol.core.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "matches")
public class MatchEntity {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "local_team_id")
    private TeamEntity localTeam;

    @ManyToOne
    @JoinColumn(name = "visitor_team_id")
    private TeamEntity visitorTeam;

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
    private TeamEntity winner;

    @ManyToOne
    @JoinColumn(name = "referee_id")
    private RefereeEntity referee;

    public int getGoalsFor(TeamEntity team) {
        if (team.equals(localTeam)) return scoreLocal;
        if (team.equals(visitorTeam)) return scoreVisitor;
        return 0;
    }

    public int getGoalsAgainst(TeamEntity team) {
        if (team.equals(localTeam)) return scoreVisitor;
        if (team.equals(visitorTeam)) return scoreLocal;
        return 0;
    }
}