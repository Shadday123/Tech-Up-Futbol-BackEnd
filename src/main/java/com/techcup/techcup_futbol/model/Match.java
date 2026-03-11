package com.techcup.techcup_futbol.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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

    public void recordEvent(MatchEvent event) {
    }

    public void finalizeMatch() {
    }

}