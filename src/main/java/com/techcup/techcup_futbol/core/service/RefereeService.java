package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.RefereeDTOs.*;

import java.util.List;

public interface RefereeService {
    RefereeResponse create(CreateRefereeRequest request);
    RefereeResponse assignToMatch(String matchId, AssignRefereeRequest request);
    RefereeResponse findById(String refereeId);
    List<RefereeResponse> findAll();
}
