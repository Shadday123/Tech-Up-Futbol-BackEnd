package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    private String uniformColors;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus status;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "plyaer_id")
    )
    private List<Player> players;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private Player captain;

}