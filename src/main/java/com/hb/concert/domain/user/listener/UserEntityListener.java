package com.hb.concert.domain.user.listener;

import com.hb.concert.domain.support.BeanUtil;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserHistory;
import com.hb.concert.domain.user.repository.UserRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class UserEntityListener {
    @PostPersist @PostUpdate @Transactional
    public void persistAndUpdate(User user) {
        UserRepository userRepository = BeanUtil.getBean(UserRepository.class);

        UserHistory userHistory = UserHistory.builder()
                .userId(user.getUserId())
                .balance(user.getBalance())
                .createAt(LocalDateTime.now())
                .build();
        userRepository.historySave(userHistory);
    }
}
