package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String type;

    private int minute;

    @ManyToOne
    private Player player;
    @ManyToOne
    private Match match;

}