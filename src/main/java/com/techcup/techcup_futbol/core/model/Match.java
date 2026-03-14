package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Data
public class Match {

    @Id
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


}