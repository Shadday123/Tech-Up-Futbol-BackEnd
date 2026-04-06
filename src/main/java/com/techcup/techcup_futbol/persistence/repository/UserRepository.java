package com.techcup.techcup_futbol.persistence.repository;

import com.techcup.techcup_futbol.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}