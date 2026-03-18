package com.techcup.techcup_futbol.core.model;

import java.util.List;

public interface FixtureStrategy {
    List<Match> generateMatches(List<Team> teams);
}
