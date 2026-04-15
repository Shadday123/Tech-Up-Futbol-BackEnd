package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Tournament;

import java.time.LocalDateTime;
import java.util.List;

public interface TournamentService {

    // ── Torneo
    Tournament create(Tournament tournament);
    Tournament findById(String id);
    List<Tournament> findAll();
    Tournament updateStatus(String id, String nextState);

    // ── Configuración
    Tournament createOrUpdateConfig(String tournamentId, String rules,
                                    LocalDateTime registrationDeadline,
                                    List<String> importantDates,
                                    List<String> matchSchedules,
                                    List<String> fields, String sanctions);
    Tournament findConfig(String tournamentId);
}
