package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, String> {
    List<Tournament> findByCurrentState(TournamentState state);
    List<Tournament> findByName(String name);
    boolean existsByName(String name);
}
