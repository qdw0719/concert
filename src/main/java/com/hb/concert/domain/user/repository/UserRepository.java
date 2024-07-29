package com.hb.concert.domain.user.repository;

import com.hb.concert.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> getUserInfo(UUID uuid);

    void save(User user);

    void saveAll(List<User> userList);

    long count();
}
