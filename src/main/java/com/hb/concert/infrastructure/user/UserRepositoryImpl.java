package com.hb.concert.infrastructure.user;

import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
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

    @Override
    public List<User> saveAll(List<User> userList) {
        return userJpaRepository.saveAll(userList);
    }

    @Override
    public Optional<User> findByUserId(UUID userId) {
        return userJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
