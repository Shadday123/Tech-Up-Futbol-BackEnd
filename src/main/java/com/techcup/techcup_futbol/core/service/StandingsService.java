package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.Standings;
import com.techcup.techcup_futbol.core.model.Team;

import java.util.List;

public interface StandingsService {

    List<Standings> findByTournamentId(String tournamentId);

    void updateFromMatch(Match match);

    void registerTeamInTournament(String tournamentId, Team team);
}
