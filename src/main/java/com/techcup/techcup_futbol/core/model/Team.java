package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Data
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String teamName;

    private String shieldUrl;

    private String uniformColors;

    @OneToOne
    private Player captain;

    @OneToMany
    private List<Player> players;

}