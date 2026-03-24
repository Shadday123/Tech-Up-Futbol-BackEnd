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

    @OneToMany
    private List<Player> players;

}