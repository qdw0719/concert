package com.hb.concert.infrastructure.user;

import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
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
}
