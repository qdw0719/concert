package com.hb.concert.presentation.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private UUID userId;
    private String token;

    @BeforeEach
    void setUp() {
        userId = userRepository.findById(1L).get().getUserId();
        token = jwtUtil.generateToken(userId, 0, 0);
    }

    @Test
    void createReservation() throws Exception {
        ReservationCommand.Create command = new ReservationCommand.Create(userId, "concert1", "detail1", List.of(1, 2, 3, 4));

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }
}
