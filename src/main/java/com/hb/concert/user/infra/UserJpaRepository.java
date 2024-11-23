package com.hb.concert.user.infra;

import com.hb.concert.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(UUID userId);
}
