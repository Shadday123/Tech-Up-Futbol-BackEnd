package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.TournamentBrackets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentBracketsRepository extends JpaRepository<TournamentBrackets, String> {
    List<TournamentBrackets> findByTournamentId(String tournamentId);
}
