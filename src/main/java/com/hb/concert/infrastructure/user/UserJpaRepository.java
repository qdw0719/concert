package com.hb.concert.infrastructure.user;

import com.hb.concert.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    long count();

    Optional<User> findByUserId(UUID userId);
}
