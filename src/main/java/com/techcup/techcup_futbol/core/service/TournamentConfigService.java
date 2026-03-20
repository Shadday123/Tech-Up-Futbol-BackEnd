package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.CreateTournamentConfigRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.TournamentConfigResponse;

public interface TournamentConfigService {
    TournamentConfigResponse createOrUpdate(String tournamentId, CreateTournamentConfigRequest request);
    TournamentConfigResponse findByTournamentId(String tournamentId);
}
