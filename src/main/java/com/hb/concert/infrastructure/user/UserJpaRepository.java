package com.hb.concert.infrastructure.user;

import com.hb.concert.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(UUID userId);
}
