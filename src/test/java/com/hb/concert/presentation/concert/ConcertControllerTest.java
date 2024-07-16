package com.hb.concert.presentation.concert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.concert.domain.queue.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest @AutoConfigureMockMvc
class ConcertControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired ConcertController concertController;

    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(concertController).build();

        // 초기 데이터 DataInitialize.class 로 대체
    }

    @Test
    void 콘서트_전체_조회() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/concerts"))
                .andExpect(status().isOk())
                .andReturn();

        List<ConcertResponse> concertList = mapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertNotNull(concertList);
        assertEquals(concertList.size(), 3);
    }

    @Test
    void 콘서트_일정_조회() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/concerts/{concertId}/details", "concert1")
                        .header("Authorization", "Bearer TestToken1"))
                .andExpect(status().isOk())
                .andReturn();

        List<ConcertDetailResponse> concertDetailList = mapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertNotNull(concertDetailList);
        assertEquals(concertDetailList.size(), 3);
        assertEquals(concertDetailList.get(0).location(), "평양");
    }

    @Test
    void 콘서트_좌석_조회() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/concerts/{concertId}/details/{detailId}/seats", "concert1", "detail1"))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, List<ConcertSeatResponse>> seatMap = mapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertNotNull(seatMap);
        assertEquals(seatMap.size(), 1);
        assertTrue(seatMap.containsKey("active"));
        assertEquals(seatMap.get("active").size(), 50);
        // 예약처리 된 좌석이 없어서 non-active는 없음
        assertFalse(seatMap.containsKey("non-active"));
    }
}