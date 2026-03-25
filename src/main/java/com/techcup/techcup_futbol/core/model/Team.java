package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(nullable = false, unique = true)
    private String teamName;

    private String shieldUrl;

    private String uniformColors;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    @OneToOne
    private Player captain;

    private List<Player> players;

}
