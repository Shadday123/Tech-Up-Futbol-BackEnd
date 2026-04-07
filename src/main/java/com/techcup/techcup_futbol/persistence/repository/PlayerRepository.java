package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository

public interface PlayerRepository extends JpaRepository<PlayerEntity, String>{

    List<PlayerEntity> findByHaveTeamFalse();
    List<PlayerEntity> findByPosition(PositionEnum position);
    List<PlayerEntity> findByEmailContaining(String email);
    Optional<PlayerEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByNumberID(Integer numberID);
    List<StudentPlayerEntity> findBySemester(Integer semester);
}
