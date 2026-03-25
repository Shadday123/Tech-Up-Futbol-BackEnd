package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.CreateTournamentConfigRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.TournamentConfigResponse;

import java.util.List;

public interface TournamentService {

    // ── Torneo
    TournamentResponse create(CreateTournamentRequest request);
    TournamentResponse findById(String id);
    List<TournamentResponse> findAll();
    TournamentResponse updateStatus(String id, String nextState);

    // ── Configuración
    TournamentConfigResponse createOrUpdateConfig(String tournamentId,
                                                  CreateTournamentConfigRequest request);
    TournamentConfigResponse findConfig(String tournamentId);
}
