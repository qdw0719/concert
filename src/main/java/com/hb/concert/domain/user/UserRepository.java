package com.hb.concert.domain.user;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    List<User> saveAll(List<User> userList);

    Optional<User> findByUserId(UUID userId);

    Optional<User> findById(Long id);

    long count();

    User save(User user);

}
