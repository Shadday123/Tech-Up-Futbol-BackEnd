package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Standings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StandingsRepository extends JpaRepository<Standings, String> {
    List<Standings> findByTournamentId(String tournamentId);
    Optional<Standings> findByTournamentIdAndTeamId(String tournamentId, String teamId);
}
