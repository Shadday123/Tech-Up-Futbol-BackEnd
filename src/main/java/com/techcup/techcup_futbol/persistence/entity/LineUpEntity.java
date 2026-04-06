package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "lineups")
public class LineUpEntity {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private MatchEntity match;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    private String formation;

    @ManyToMany
    @JoinTable(
            name = "lineup_starters",
            joinColumns = @JoinColumn(name = "lineup_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<PlayerEntity> starters;

    @ManyToMany
    @JoinTable(
            name = "lineup_substitutes",
            joinColumns = @JoinColumn(name = "lineup_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<PlayerEntity> substitutes;

    @ElementCollection
    @CollectionTable(name = "lineup_field_positions", joinColumns = @JoinColumn(name = "lineup_id"))
    @Column(name = "position_entry")
    private List<String> fieldPositions;
}