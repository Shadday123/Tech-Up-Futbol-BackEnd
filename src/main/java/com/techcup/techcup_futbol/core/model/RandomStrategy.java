package com.techcup.techcup_futbol.core.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomStrategy implements FixtureStrategy {

    @Override
    public List<Match> generateMatches(List<Team> teams) {
        List<Team> shuffledTeams = new ArrayList<>(teams);
        Collections.shuffle(shuffledTeams, new SecureRandom());

        // Usamos Streams para emparejar de dos en dos
        return IntStream.range(0, shuffledTeams.size() / 2)
                .mapToObj(i -> {
                    Match match = new Match();

                    match.setLocalTeam(shuffledTeams.get(i * 2));
                    match.setVisitorTeam(shuffledTeams.get(i * 2 + 1));

                    return match;
                })
                .collect(Collectors.toList());

    }
}