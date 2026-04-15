package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Referee;

import java.util.List;

public interface RefereeService {
    Referee create(String fullname, String email);
    Referee assignToMatch(String matchId, String refereeId);
    Referee findById(String refereeId);
    List<Referee> findAll();
}
