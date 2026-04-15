package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository

public interface LineupRepository extends JpaRepository<LineUpEntity, String> {
    Optional<LineUpEntity> findByMatchIdAndTeamId(String matchId, String teamId);
    List<LineUpEntity> findByMatchId(String matchId);
    boolean existsByMatchIdAndTeamId(String matchId, String teamId);
}
