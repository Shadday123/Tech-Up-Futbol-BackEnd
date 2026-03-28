package com.techcup.techcup_futbol.repository;

import com.techcup.techcup_futbol.core.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTeamId(String teamId);
}
