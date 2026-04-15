package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.TournamentBrackets;
import com.techcup.techcup_futbol.persistence.entity.TournamentBracketsEntity;

import java.util.List;

public interface BracketService {
    List<TournamentBracketsEntity> generate(String tournamentId, int teamsCount);
    List<TournamentBrackets> findByTournamentId(String tournamentId);
    List<TournamentBrackets> advanceWinner(String tournamentId, String matchId);
}
