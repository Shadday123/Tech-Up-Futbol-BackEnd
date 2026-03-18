package com.techcup.techcup_futbol.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EliminationStrategy implements FixtureStrategy{
    @Override
    public List<Match> generateMatches(List<Team> teams) {
        // Validamos que tengamos al menos 2 equipos para jugar
        if (teams == null || teams.size() < 2) return new ArrayList<>();

        int half = teams.size() / 2;

        // Usamos IntStream para crear los índices
        return IntStream.range(0, half)
                .mapToObj(i -> {
                    Match match = new Match();

                    match.setLocalTeam(teams.get(i));
                    match.setVisitorTeam(teams.get(teams.size() - 1 - i));

                    return match;
                })
                .collect(Collectors.toList());
    }
}

