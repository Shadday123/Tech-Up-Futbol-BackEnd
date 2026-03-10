package com.techcup.techcup_futbol.util;
import com.techcup.techcup_futbol.model.Team;

import java.util.ArrayList;
import java.util.List;

public class DataSimulator {

    public static List<Team> teams = new ArrayList<>();

    static {
        teams.add(new Team(1,"Barcelona"));
        teams.add(new Team(2,"Real Madrid"));
        teams.add(new Team(3,"Bayern Munich"));
    }
}
