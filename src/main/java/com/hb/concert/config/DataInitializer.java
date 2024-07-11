package com.hb.concert.config;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.concert.*;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration @Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final ConcertDetailRepository concertDetailRepository;
    private final ConcertSeatRepository concertSeatRepository;

    public DataInitializer(UserRepository userRepository, ConcertRepository concertRepository, ConcertDetailRepository concertDetailRepository, ConcertSeatRepository concertSeatRepository) {
        this.userRepository = userRepository;
        this.concertRepository = concertRepository;
        this.concertDetailRepository = concertDetailRepository;
        this.concertSeatRepository = concertSeatRepository;
    }

    @Bean @Transactional
    public ApplicationRunner init() {
        return args -> {
            List<User> userList = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                userList.add(User.builder().userId(UUID.randomUUID()).balance(0).build());
            }

            userRepository.saveAll(userList);
            log.info("Successfully saved all users");

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

            int idIndex = 1;
            List<ConcertDetail> concertDetailList = new ArrayList<>();
            for (Concert concert : concertList) {
                String concertId = concert.getConcertId();
                concertDetailList.add(
                        ConcertDetail.builder()
                                .concertDetailId("detail" + idIndex)
                                .concertId(concertId)
                                .location("결기 양주시")
                                .concertDate(LocalDate.now().plusMonths(idIndex))
                                .validState(ValidState.VALID)
                                .build()
                );
                idIndex++;
            }

            log.info("concertDetailList.size(): {}", concertDetailList.size());

            concertDetailRepository.saveAll(concertDetailList);
            log.info("Successfully saved all concert details");

            List<ConcertSeat> concertSeatList = new ArrayList<>();
            for (Concert concert : concertList) {
                String concertId = concert.getConcertId();

                for (ConcertDetail concertDetail : concertDetailList) {
                    String concertDetailId = concertDetail.getConcertDetailId();

                    for (int iter = 0; iter < 50; iter++) {
                        concertSeatList.add(
                                ConcertSeat.builder()
                                        .concertSeatId(iter + 1)
                                        .concertId(concertId)
                                        .concertDetailId(concertDetailId)
                                        .useYn(UseYn.Y)
                                        .price(30000)
                                        .build()
                        );
                    }
                }
            }

            log.info("concertSeatList.size(): {}", concertSeatList.size());

            concertSeatRepository.saveAll(concertSeatList);
            log.info("Successfully saved all concert seats");
        };
    }
}
