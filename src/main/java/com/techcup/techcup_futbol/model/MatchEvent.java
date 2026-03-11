package com.techcup.techcup_futbol.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class MatchEvent {

    @Id
    private String id;

    private String type;

    private int minute;

    private Player player;

    private Match match;

}