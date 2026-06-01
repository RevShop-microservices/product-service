package com.example.product_service.service;

import com.example.product_service.dto.AdminAnalyticsDTO;
import com.example.product_service.repository.OrderRepository;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class AdminAnalyticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminAnalyticsService adminAnalyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAdminAnalytics() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByRole("SELLER")).thenReturn(15);
        when(productRepository.count()).thenReturn(500L);
        when(orderRepository.count()).thenReturn(250L);

        AdminAnalyticsDTO analytics = adminAnalyticsService.getAdminAnalytics();

        assertNotNull(analytics);
        assertEquals(100L, analytics.getTotalUsers());
        assertEquals(15L, analytics.getActiveSellers());
        assertEquals(500L, analytics.getTotalProducts());
        assertEquals(250L, analytics.getTotalOrders());
    }
}
