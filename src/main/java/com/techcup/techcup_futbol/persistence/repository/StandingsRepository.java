package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StandingsRepository extends JpaRepository<StandingsEntity, String> {
    List<StandingsEntity> findByTournamentId(String tournamentId);
    Optional<StandingsEntity> findByTournamentIdAndTeamId(String tournamentId, String teamId);

    @Query("SELECT s FROM StandingsEntity s JOIN s.team.players p WHERE p.id = :playerId")
    List<StandingsEntity> findByTeamPlayersId(@Param("playerId") String playerId);
}
