package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository

public interface PlayerRepository extends JpaRepository<Player, String>{

    List<Player> findByHaveTeamFalse();
    List<Player> findByPosition(PositionEnum position);
    List<Player> findByEmailContaining(String email);
    Optional<Player> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByNumberID(Integer numberID);
    List<StudentPlayer> findBySemester(Integer semester);
}
