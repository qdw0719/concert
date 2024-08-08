package com.hb.concert.user.infra;

import com.hb.concert.user.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryJpaRepository extends JpaRepository<UserHistory, Long> {
}
