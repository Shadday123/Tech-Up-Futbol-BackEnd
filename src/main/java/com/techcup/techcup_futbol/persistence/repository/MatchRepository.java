package com.techcup.techcup_futbol.persistence.repository;
import com.techcup.techcup_futbol.persistence.entity.*;
import com.techcup.techcup_futbol.core.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {
    List<MatchEntity> findByLocalTeamIdOrVisitorTeamId(String localTeamId, String visitorTeamId);
    List<MatchEntity> findByStatus(MatchStatus status);
    List<MatchEntity> findByReferee(RefereeEntity referee);
    boolean existsByIdAndRefereeIsNotNull(String id);
}
