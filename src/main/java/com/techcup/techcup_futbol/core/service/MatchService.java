package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.*;
import com.techcup.techcup_futbol.core.model.Match;

import java.util.List;

public interface MatchService {

    MatchResponse create(CreateMatchRequest request);

    MatchResponse registerResult(String matchId, RegisterResultRequest request);

    MatchResponse findById(String matchId);

    List<MatchResponse> findAll();

    List<MatchResponse> findByTeamId(String teamId);

    boolean isResultRegistered(String matchId);

    void registerMatch(Match match);

    // Expone el mapa interno de partidos para RefereeService y BracketService
    java.util.Map<String, com.techcup.techcup_futbol.core.model.Match> getMatches();
}