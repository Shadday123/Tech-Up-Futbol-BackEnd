package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Team;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TeamRepository extends JpaRepository<Team, String>{
    boolean existsByName(String name);
    List<Team> findByCaptainId(String captainId);
}