package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.MatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent, String> {
    List<MatchEvent> findByMatchId(String matchId);
    void deleteByMatchId(String matchId);
}
