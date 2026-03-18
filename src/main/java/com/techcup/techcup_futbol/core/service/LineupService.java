package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.LineupDTOs.CreateLineupRequest;
import com.techcup.techcup_futbol.Controller.dto.LineupDTOs.LineupResponse;

public interface LineupService {
    LineupResponse create(CreateLineupRequest request);
    LineupResponse findByMatchAndTeam(String matchId, String teamId);
    LineupResponse findRivalLineup(String matchId, String teamId);
}
