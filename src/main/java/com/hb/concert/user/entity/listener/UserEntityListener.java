package com.hb.concert.user.entity.listener;

import com.hb.concert.support.BeanUtil;
import com.hb.concert.user.entity.User;
import com.hb.concert.user.entity.UserHistory;
import com.hb.concert.user.entity.repository.UserRepository;
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
