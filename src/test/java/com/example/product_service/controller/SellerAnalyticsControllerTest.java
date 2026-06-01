package com.example.product_service.controller;

import com.example.product_service.dto.SellerAnalyticsDTO;
import com.example.product_service.security.JwtUtil;
import com.example.product_service.service.SellerAnalyticsService;
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
        controllers = SellerAnalyticsController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        }
)
class SellerAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerAnalyticsService sellerAnalyticsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Test
    void testGetAnalytics_Success() throws Exception {
        SellerAnalyticsDTO analytics = SellerAnalyticsDTO.builder()
                .sellerId(10L)
                .totalProducts(5)
                .totalRevenue(500.0)
                .build();

        when(sellerAnalyticsService.getSellerAnalytics(10L)).thenReturn(analytics);

        mockMvc.perform(get("/api/seller/analytics")
                        .param("sellerId", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerId").value(10))
                .andExpect(jsonPath("$.totalProducts").value(5))
                .andExpect(jsonPath("$.totalRevenue").value(500.0));
    }
}
