package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Lineup;
import com.techcup.techcup_futbol.core.model.Match;

import java.util.List;

public interface LineupService {
    Lineup create(String matchId, String teamId, String formation,
                  List<String> starterIds, List<String> substituteIds,
                  List<String> fieldPositions);
    Lineup findByMatchAndTeam(String matchId, String teamId);
    Lineup findRivalLineup(String matchId, String teamId);
    void registerMatch(Match match);
}
