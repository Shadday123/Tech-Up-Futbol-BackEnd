package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.TournamentBrackets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface TournamentBracketsRepository extends JpaRepository<TournamentBrackets, String> {
    List<TournamentBrackets> findByTournamentId(String tournamentId);
}
