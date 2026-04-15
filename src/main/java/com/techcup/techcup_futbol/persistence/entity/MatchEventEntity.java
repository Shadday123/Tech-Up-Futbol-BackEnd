package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "match_events")
public class MatchEventEntity {

    @Id
    private String id;

    private String type;

    private int minute;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private MatchEntity match;
}