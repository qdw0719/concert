package com.hb.concert.presentation.concert;

import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    private UUID userId;
    private String token;

    @BeforeEach
    void setUp() {
        User user = userRepository.findById(1L).orElseThrow();
        userId = user.getUserId();
        token = jwtUtil.generateToken(userId, 0, 0);

        QueueToken queueToken = new QueueToken().builder()
                .userId(userId)
                .token(token)
                .isActive(UseYn.Y)
                .position(0)
                .waitTime(0)
                .status(QueueToken.TokenStatus.PROCESS)
                .build();
        queueTokenRepository.save(queueToken);
    }

    @Test
    void getConcerts() throws Exception {
        mockMvc.perform(get("/api/concerts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getAvailableDetails() throws Exception {
        String concertId = "concert1";
        mockMvc.perform(get("/api/concerts/{concertId}/details", concertId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getConcertSeats() throws Exception {
        String concertId = "concert1";
        String detailId = "detail1";
        mockMvc.perform(get("/api/concerts/{concertId}/details/{detailId}/seats", concertId, detailId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }
}