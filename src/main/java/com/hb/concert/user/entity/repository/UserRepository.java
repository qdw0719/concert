package com.hb.concert.user.entity.repository;

import com.hb.concert.user.entity.User;
import com.hb.concert.user.entity.UserHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> getUserInfo(UUID uuid);

    void save(User user);

    void saveAll(List<User> userList);

    long count();

    Optional<User> getUserInfoById(long id);

    void historySave(UserHistory userHistory);

    void historySaveAll(List<UserHistory> userHistoryList);
}
