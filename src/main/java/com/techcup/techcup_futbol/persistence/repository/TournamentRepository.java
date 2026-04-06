package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.*;
import com.techcup.techcup_futbol.core.model.TournamentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, String> {
    List<TournamentEntity> findByCurrentState(TournamentState state);
    List<TournamentEntity> findByName(String name);
    boolean existsByName(String name);
}
