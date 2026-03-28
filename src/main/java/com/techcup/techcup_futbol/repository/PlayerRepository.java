package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PlayerRepository extends JpaRepository<Player, String>{

    List<Player> findByHaveTeamFalse();
    List<Player> findByPosition(PositionEnum position);
    List<Player> findByEmailContaining(String email);
    List<StudentPlayer> findBySemester(Integer semester);
}