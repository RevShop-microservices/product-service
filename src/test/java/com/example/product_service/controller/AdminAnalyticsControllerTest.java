package com.example.product_service.controller;

import com.example.product_service.dto.AdminAnalyticsDTO;
import com.example.product_service.security.JwtUtil;
import com.example.product_service.service.AdminAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AdminAnalyticsController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        }
)
class AdminAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAnalyticsService adminAnalyticsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Test
    void testGetAdminAnalytics_Success() throws Exception {
        AdminAnalyticsDTO analytics = AdminAnalyticsDTO.builder()
                .totalUsers(100L)
                .activeSellers(15L)
                .totalProducts(500L)
                .totalOrders(250L)
                .build();

        when(adminAnalyticsService.getAdminAnalytics()).thenReturn(analytics);

        mockMvc.perform(get("/api/admin/analytics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.activeSellers").value(15))
                .andExpect(jsonPath("$.totalProducts").value(500))
                .andExpect(jsonPath("$.totalOrders").value(250));
    }
}
