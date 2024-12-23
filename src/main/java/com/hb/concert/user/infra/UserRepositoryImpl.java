package com.hb.concert.user.infra;

import com.hb.concert.user.entity.User;
import com.hb.concert.user.entity.UserHistory;
import com.hb.concert.user.entity.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserHistoryJpaRepository userHistoryJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserHistoryJpaRepository userHistoryJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.userHistoryJpaRepository = userHistoryJpaRepository;
    }

    @Override public Optional<User> getUserInfo(UUID userId) {
        return userJpaRepository.findByUserId(userId);
    }

    @Override public void save(User user) {
        userJpaRepository.save(user);
    }

    @Override public void saveAll(List<User> userList) {
        userJpaRepository.saveAll(userList);
    }

    @Override public long count() {
        return userJpaRepository.count();
    }

    @Override public Optional<User> getUserInfoById(long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public void historySave(UserHistory userHistory) {
        userHistoryJpaRepository.save(userHistory);
    }

    @Override
    public void historySaveAll(List<UserHistory> userHistoryList) {
        userHistoryJpaRepository.saveAll(userHistoryList);
    }
}
