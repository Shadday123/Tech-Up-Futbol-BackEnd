package com.techcup.techcup_futbol.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Match {

    private String id;

    private Team localTeam;

    private Team visitorTeam;

    private LocalDateTime dateTime;

    private int scoreLocal;
    private int scoreVisitor;

    private int yellowCards;
    private int redCards;

    private int field;

    private MatchStatus status = MatchStatus.SCHEDULED;

    private Team winner;

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
