package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.MatchDTOs.*;

import java.util.List;

public interface MatchService {
    MatchResponse create(CreateMatchRequest request);
    MatchResponse registerResult(String matchId, RegisterResultRequest request);
    MatchResponse findById(String matchId);
    List<MatchResponse> findAll();
    List<MatchResponse> findByTeamId(String teamId);
}
