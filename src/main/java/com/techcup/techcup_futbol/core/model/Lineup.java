package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Lineup {

    private String id;

    private Match match;

    private Team team;

    private String formation;

    private List<Player> starters;

    private List<Player> substitutes;

    private List<String> fieldPositions;
}
