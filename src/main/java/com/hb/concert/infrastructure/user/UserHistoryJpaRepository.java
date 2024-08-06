package com.hb.concert.infrastructure.user;

import com.hb.concert.domain.user.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryJpaRepository extends JpaRepository<UserHistory, Long> {
}
