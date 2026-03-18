package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    private Team localTeam;
    @OneToOne
    private Team visitorTeam;

    private LocalDateTime dateTime;

    private int scoreLocal;
    private int scoreVisitor;

    private int yellowCards;
    private int redCards;

    private int field;


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