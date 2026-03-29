package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateLineupRequest;
import com.techcup.techcup_futbol.Controller.dto.LineupResponse;
import com.techcup.techcup_futbol.core.model.Match;

public interface LineupService {
    LineupResponse create(CreateLineupRequest request);
    LineupResponse findByMatchAndTeam(String matchId, String teamId);
    LineupResponse findRivalLineup(String matchId, String teamId);
    void registerMatch(Match match);
}
