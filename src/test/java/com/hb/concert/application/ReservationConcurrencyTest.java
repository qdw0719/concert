package com.hb.concert.application;

import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.application.reservation.facade.ReservationFacade;
import com.hb.concert.domain.reservation.service.ReservationService;
import com.hb.concert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired private ReservationFacade reservationFacade;
    @Autowired private ReservationService reservationService;
    @Autowired private UserService userService;

    private List<Integer> seatIdList;
    private List<UUID> users;
    private String concertId;
    private String concertDetailId;

    @BeforeEach
    void setUp() {
        // given
        seatIdList = List.of(1, 2, 3, 4);
        users = List.of(
                userService.findById(1L).get().getUserId(),
                userService.findById(2L).get().getUserId(),
                userService.findById(3L).get().getUserId(),
                userService.findById(4L).get().getUserId(),
                userService.findById(5L).get().getUserId(),
                userService.findById(6L).get().getUserId(),
                userService.findById(7L).get().getUserId(),
                userService.findById(8L).get().getUserId(),
                userService.findById(9L).get().getUserId(),
                userService.findById(10L).get().getUserId()
        );
        concertId = "concert1";
        concertDetailId = "detail1";

        // data setting은 DataInitializer.java 이용.
    }

    @Test
    void 예약_동시성_테스트() throws ExecutionException, InterruptedException {
        // given
        int numberOfThreads = 10;

        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            int index = i;
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    reservationFacade.createReservation(
                            new ReservationCommand.Create(users.get(index), concertId, concertDetailId, seatIdList)
                    );
                    successCount.incrementAndGet();
                    return true;
                } catch (Exception e) {
                    // 예외 처리
                    failCount.incrementAndGet();
                    return false;
                }
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        // then
        ReservationCommand.GetReservationInfo reservation = reservationService.getReservationInfo(users.get(0));
        assertNotNull(reservation);
        assertEquals(users.get(0), reservation.userId());
        assertEquals(seatIdList.size(), reservation.seatId().size());

        // 성공한 예약과 실패한 예약의 수를 검증
        assertEquals(1, successCount.get());
        assertEquals(numberOfThreads - 1, failCount.get());
    }
}
