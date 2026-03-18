package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.StandingsDTOs.StandingsResponse;
import com.techcup.techcup_futbol.core.model.Match;

public interface StandingsService {
    StandingsResponse findByTournamentId(String tournamentId);
    void updateFromMatch(Match match);
}
