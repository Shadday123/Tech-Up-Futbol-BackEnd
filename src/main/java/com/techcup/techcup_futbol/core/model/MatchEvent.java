package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class MatchEvent {

    @Id
    private String id;

    private String type;

    private int minute;

    @ManyToOne
    private Player player;
    @ManyToOne
    private Match match;

}