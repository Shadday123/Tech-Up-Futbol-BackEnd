package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefereeRepository extends JpaRepository<RefereeEntity, String> {
    boolean existsByEmail(String email);
    Optional<RefereeEntity> findByEmail(String email);
}
