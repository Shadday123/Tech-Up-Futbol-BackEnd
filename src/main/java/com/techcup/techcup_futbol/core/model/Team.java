package com.techcup.techcup_futbol.core.model;

import lombok.Data;
import java.util.List;

@Data
public class Team {


    private String id;

    private String teamName;

    private String shieldUrl;

    private List<String> uniformColors;

    private Player captain;

    private TeamStatus status;


    private List<Player> players;
}
