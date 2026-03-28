package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.MatchStatus;
import com.techcup.techcup_futbol.core.model.Referee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, String> {
    List<Match> findByLocalTeamIdOrVisitorTeamId(String localTeamId, String visitorTeamId);
    List<Match> findByStatus(MatchStatus status);
    List<Match> findByReferee(Referee referee);
    boolean existsByIdAndRefereeIsNotNull(String id);
}
