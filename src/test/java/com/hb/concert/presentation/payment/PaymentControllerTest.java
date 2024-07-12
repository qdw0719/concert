package com.hb.concert.presentation.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.concert.application.payment.command.PaymentCommand;
import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc
class PaymentControllerTest {

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
    void createPayment() throws Exception {
        PaymentCommand.CreatePayment command = new PaymentCommand.CreatePayment(userId, 1, 30000, "reservation_0001", token);

        mockMvc.perform(post("/api/payments/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }
}
