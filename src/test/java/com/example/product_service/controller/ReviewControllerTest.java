package com.example.product_service.controller;

import com.example.product_service.dto.*;
import com.example.product_service.security.JwtUtil;
import com.example.product_service.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ReviewController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        }
)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Test
    void testAddReview_Success() throws Exception {
        ReviewRequestDTO req = new ReviewRequestDTO();
        req.setProductId("p1");
        req.setUserId(1L);
        req.setRating(5);
        req.setComment("Awesome!");
        ReviewResponseDTO resp = ReviewResponseDTO.builder()
                .productId("p1")
                .userId(1L)
                .rating(5)
                .comment("Awesome!")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.addReview(any(ReviewRequestDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("p1"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Awesome!"));
    }

    @Test
    void testGetReviews_Success() throws Exception {
        ReviewResponseDTO resp = ReviewResponseDTO.builder()
                .productId("p1")
                .userId(1L)
                .rating(4)
                .comment("Good")
                .createdAt(LocalDateTime.now())
                .build();
        when(reviewService.getReviews("p1")).thenReturn(Arrays.asList(resp));

        mockMvc.perform(get("/api/reviews/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("p1"))
                .andExpect(jsonPath("$[0].comment").value("Good"));
    }

    @Test
    void testGetProductRating_Success() throws Exception {
        ProductRatingDTO rating = new ProductRatingDTO(4.5, 10);
        when(reviewService.getProductRating("p1")).thenReturn(rating);

        mockMvc.perform(get("/api/reviews/p1/rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.totalReviews").value(10));
    }

    @Test
    void testDeleteReview_Success() throws Exception {
        ReviewRequestDTO req = new ReviewRequestDTO();
        req.setProductId("p1");
        req.setUserId(1L);

        mockMvc.perform(delete("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).deleteReview("p1", 1L);
    }
}
