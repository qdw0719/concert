package com.hb.concert.support.config;

import com.hb.concert.concert.entity.Concert;
import com.hb.concert.concert.entity.ConcertDetail;
import com.hb.concert.concert.entity.ConcertReservation;
import com.hb.concert.concert.entity.ConcertSeatConfig;
import com.hb.concert.concert.entity.repository.ConcertRepository;
import com.hb.concert.payment.entity.Payment;
import com.hb.concert.payment.entity.repository.PaymentRepository;
import com.hb.concert.user.entity.User;
import com.hb.concert.user.entity.UserHistory;
import com.hb.concert.user.entity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Configuration @Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final PaymentRepository paymentRepository;

    public DataInitializer(UserRepository userRepository, ConcertRepository concertRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.concertRepository = concertRepository;
        this.paymentRepository = paymentRepository;
    }

    @Bean @Transactional
    public ApplicationRunner init() {
        return args -> {
            long userCount = userRepository.count();
            if (userCount > 0) {
                log.info("User count : {}", userRepository.count());
            } else {
                List<User> userList = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    userList.add(User.builder().userId(UUID.randomUUID()).balance(0).build());
                }
                userRepository.saveAll(userList);
                log.info("Successfully saved all users");
            }

            List<UserHistory> userHistoryList = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                userHistoryList.add(
                        UserHistory.builder()
                                .userId(UUID.randomUUID())
                                .amount(i % 100)
                                .balance((i % 100) * 10)
                                .createAt(LocalDateTime.now())
                                .build()
                );

                if ((i + 1) % 1000 == 0) {
                    userRepository.historySaveAll(userHistoryList);
                    userHistoryList.clear();
                    Thread.sleep(50);
                }
            }
            if (!userHistoryList.isEmpty()) {
                userRepository.historySaveAll(userHistoryList);
            }
            log.info("Successfully saved all user histories");

            long concertCount = concertRepository.concertCount();
            if (concertCount > 0) {
                log.info("Concert count : {}", concertRepository.concertCount());
            } else {
                Concert concert1 = Concert.builder()
                        .concertId("concert1")
                        .concertName("김일성 콘서트")
                        .artist("김일성")
                        .build();
                Concert concert2 = Concert.builder()
                        .concertId("concert2")
                        .concertName("김정일 콘서트")
                        .artist("김정일")
                        .build();
                Concert concert3 = Concert.builder()
                        .concertId("concert3")
                        .concertName("김정은 콘서트")
                        .artist("김정은")
                        .build();

                List<Concert> concertList = List.of(concert1, concert2, concert3);
                log.info("concertList.size(): {}", concertList.size());
                concertRepository.saveAll(concertList);
                log.info("Successfully saved all concerts");
            }

            long concertDetailCount = concertRepository.detailCount();
            if (concertDetailCount > 0) {
                log.info("ConcertDetail count : {}", concertRepository.detailCount());
            } else {
                List<ConcertDetail> concertDetailList = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    int idIndex = i + 1;
                    int index = (i % 3) + 1;
                    concertDetailList.add(
                            ConcertDetail.builder()
                                    .concertId("concert" + index)
                                    .concertDetailId("concertDetail" + idIndex)
                                    .concertDate(getRandomDate())
                                    .build()
                    );
                }
                log.info("concertDetailList.size(): {}", concertDetailList.size());
                concertRepository.detailSaveAll(concertDetailList);
                log.info("Successfully saved all concert details");
            }

            long concertSeatCount = concertRepository.seatCount();
            if (concertSeatCount > 0) {
                log.info("Concert seat count : {}", concertRepository.seatCount());
            } else {
                List<ConcertSeatConfig> concertSeatConfigList = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    int seatId = i + 1;
                    concertSeatConfigList.add(
                            ConcertSeatConfig.builder()
                                    .seatId(seatId)
                                    .price(15000)
                                    .build()
                    );
                }
                log.info("concertSeatList.size(): {}", concertSeatConfigList.size());
                concertRepository.seatSaveAll(concertSeatConfigList);
                log.info("Successfully saved all concert seat config");
            }

            List<ConcertReservation> concertReservationList = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                concertReservationList.add(
                        ConcertReservation.builder()
                                .userId(UUID.randomUUID())
                                .concertDetailId("concertDetail" + (i % 1000 + 1))
                                .reservationTime(LocalDateTime.now())
                                .reservedSeatId(List.of(i % 100))
                                .build()
                );

                if ((i + 1) % 1000 == 0) {
                    concertRepository.reservationSaveAll(concertReservationList);
                    concertReservationList.clear();
                    Thread.sleep(50);
                }
            }
            if (!concertReservationList.isEmpty()) {
                concertRepository.reservationSaveAll(concertReservationList);
            }
            log.info("Successfully saved all concert reservations");


            List<Payment> paymentList = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                paymentList.add(
                        Payment.builder()
                                .reservationId("reservation" + (i + 1))
                                .effectiveTime(LocalDateTime.now().plusMinutes(5))
                                .build()
                );

                if ((i + 1) % 1000 == 0) {
                    paymentRepository.saveAll(paymentList);
                    paymentList.clear();
                    Thread.sleep(50);
                }
            }
            if (!paymentList.isEmpty()) {
                paymentRepository.saveAll(paymentList);
            }
            log.info("Successfully saved all payments");
        };
    }

    public static LocalDate getRandomDate() {
        LocalDate startDate = LocalDate.of(2024, 7, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        List<LocalDate> availableDates = new ArrayList<>();
        for (long i = 0; i <= daysBetween; i++) {
            availableDates.add(startDate.plusDays(i));
        }
        Collections.shuffle(availableDates); // 리스트를 무작위로 섞음

        return availableDates.remove(0);
    }
}


