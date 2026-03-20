package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.StandingsDTOs.StandingsResponse;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.Team;

public interface StandingsService {

    StandingsResponse findByTournamentId(String tournamentId);

    void updateFromMatch(Match match);

    void registerTeamInTournament(String tournamentId, Team team);
}