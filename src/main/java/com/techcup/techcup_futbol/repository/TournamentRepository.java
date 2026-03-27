package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TournamentRepository extends JpaRepository<Tournament, UUID>{

    List<Tournament> findByState(TournamentState state);
    List<Tournament> findByName(String name);
    boolean existsByName(String name);

}