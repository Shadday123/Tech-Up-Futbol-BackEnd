package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.BracketResponse;
import com.techcup.techcup_futbol.Controller.dto.GenerateBracketRequest;

public interface BracketService {
    BracketResponse generate(String tournamentId, GenerateBracketRequest request);
    BracketResponse findByTournamentId(String tournamentId);
    BracketResponse advanceWinner(String tournamentId, String matchId);
}
