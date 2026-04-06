package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, String> {
    boolean existsByTeamName(String teamName);
    List<TeamEntity> findByCaptainId(String captainId);
}
