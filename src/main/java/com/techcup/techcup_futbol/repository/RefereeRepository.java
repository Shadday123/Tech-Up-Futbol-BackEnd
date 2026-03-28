package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Referee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefereeRepository extends JpaRepository<Referee, String> {
    boolean existsByEmail(String email);
    Optional<Referee> findByEmail(String email);
}
