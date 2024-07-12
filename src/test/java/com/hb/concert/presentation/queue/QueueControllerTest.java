package com.hb.concert.presentation.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.concert.application.queue.command.QueueCommand;
import com.hb.concert.common.exception.CustomException;
import com.hb.concert.common.exception.ExceptionMessage;
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

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private UUID userId;

    private String token;

    @BeforeEach
    void setUp() {
        Optional<User> userOptional = userRepository.findById(1L);
        User user = userOptional.orElseThrow(() -> new CustomException.NotFoundException(ExceptionMessage.NOT_FOUND.replace("{msg}", "유저"))); // Handling user not found
        userId = user.getUserId();

        token = jwtUtil.generateToken(userId, 0, 0);
    }

    @Test
    void generateToken() throws Exception {
        QueueCommand.Generate command = new QueueCommand.Generate(userId);

        mockMvc.perform(post("/queue/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void processCompletedToken() throws Exception {
        QueueCommand.TokenCompleted command = new QueueCommand.TokenCompleted(userId, token);

        mockMvc.perform(post("/queue/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());
    }
}
