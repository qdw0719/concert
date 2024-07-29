package com.hb.concert.support.config;

import com.hb.concert.domain.concert.*;
import com.hb.concert.domain.concert.repository.ConcertRepository;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Configuration @Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;

    public DataInitializer(UserRepository userRepository, ConcertRepository concertRepository) {
        this.userRepository = userRepository;
        this.concertRepository = concertRepository;
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
                for (int i = 0; i < 100; i++) {
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
                for (int i = 0; i < 100; i++) {
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
        };
    }

    public static LocalDate getRandomDate() {
        LocalDate startDate = LocalDate.of(2024, 7, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        List<LocalDate> availableDates = new ArrayList<>();
        for (long i = 0; i <= daysBetween; i++) {
            availableDates.add(startDate.plusDays(i));
        }
        Collections.shuffle(availableDates); // 리스트를 무작위로 섞음

        return availableDates.remove(0);
    }
}


