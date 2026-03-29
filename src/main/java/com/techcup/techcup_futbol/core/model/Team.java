package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "teams")
public class Team {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String teamName;

    private String shieldUrl;

    @ElementCollection
    @CollectionTable(name = "team_uniform_colors", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "hex_color", length = 7)
    private List<String> uniformColors;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private Player captain;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players;
}
