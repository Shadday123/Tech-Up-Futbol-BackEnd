package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, String> {
    boolean existsByTeamName(String teamName);
    List<Team> findByCaptainId(String captainId);
}
