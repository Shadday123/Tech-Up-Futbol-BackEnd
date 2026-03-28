package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    boolean existsByTeamName(String teamName);
    List<Team> findByCaptainId(String captainId);
}
