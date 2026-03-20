package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import java.util.List;

public interface TournamentService {
    TournamentResponse create(CreateTournamentRequest request);
    TournamentResponse findById(String id);
    List<TournamentResponse> findAll();
    TournamentResponse updateStatus(String id, String nextState);
}
