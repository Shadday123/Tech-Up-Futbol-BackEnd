package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByTeamId(String teamId);
}
