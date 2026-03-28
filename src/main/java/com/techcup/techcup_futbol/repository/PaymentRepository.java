package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTeamId(String teamId);
}
