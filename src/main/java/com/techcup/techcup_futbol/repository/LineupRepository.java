package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Lineup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineupRepository extends JpaRepository<Lineup, String> {
    Optional<Lineup> findByMatchIdAndTeamId(String matchId, String teamId);
    List<Lineup> findByMatchId(String matchId);
    boolean existsByMatchIdAndTeamId(String matchId, String teamId);
}
