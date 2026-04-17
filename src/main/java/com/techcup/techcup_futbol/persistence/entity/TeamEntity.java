package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import com.techcup.techcup_futbol.core.model.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "teams")
public class TeamEntity  {

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
    private PlayerEntity captain;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "players_id")
    )
    private List<PlayerEntity> players;
}