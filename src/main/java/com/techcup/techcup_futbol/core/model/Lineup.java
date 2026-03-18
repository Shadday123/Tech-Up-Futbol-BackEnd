package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Lineup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private Match match;

    @ManyToOne
    private Team team;

    private String formation;

    @ManyToMany
    @JoinTable(name = "lineup_starters")
    private List<Player> starters;

    @ManyToMany
    @JoinTable(name = "lineup_substitutes")
    private List<Player> substitutes;

    @ElementCollection
    private List<String> fieldPositions;
}
