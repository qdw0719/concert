package com.hb.concert.application;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserConcurrencyTest {

    @Autowired  private UserService userService;

    private final int THREAD_COUNT = 10;

    private UUID userId;
    private UserCommand.SetUserBalance command;

    @BeforeEach
    void setUp() {
        // given
        User user = userService.findById(1L).get();
        userId = user.getUserId();
        command = new UserCommand.SetUserBalance(userId, 10000);
    }

    @Test @Transactional
    public void 유저_잔액충전_테스트() {
        // given
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    userService.chargeBalance(command);
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.incrementAndGet();
                }
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        User result = userService.findById(1L).get();
        assertEquals(10000, result.getBalance());
        assertEquals(1, successCount.get());
        assertEquals(9, failCount.get());
    }
}

// 테스트 결과 log
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-1] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 7 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-8] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 5 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-6] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 5 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-7] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 5 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-4] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 7 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-5] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 6 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-2] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 7 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [nPool-worker-10] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 5 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-3] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 7 ms
//2024-07-24T23:08:16.677+09:00  INFO 23600 --- [onPool-worker-9] c.h.c.domain.user.service.UserService    : 총 걸린 시간 >>> 5 ms