package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.*;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository

public interface PlayerRepository extends JpaRepository<PlayerEntity, String>{

    List<PlayerEntity> findByHaveTeamFalse();
    List<PlayerEntity> findByPosition(PositionEnum position);
    List<PlayerEntity> findByEmailContaining(String email);
    List<StudentPlayerEntity> findBySemester(Integer semester);
}