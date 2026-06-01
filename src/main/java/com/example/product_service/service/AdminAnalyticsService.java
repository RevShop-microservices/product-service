package com.example.product_service.service;

import com.example.product_service.dto.AdminAnalyticsDTO;
import com.example.product_service.repository.OrderRepository;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public AdminAnalyticsDTO getAdminAnalytics() {
        long totalUsers = userRepository.count();
        long activeSellers = userRepository.countByRole("SELLER");
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        return AdminAnalyticsDTO.builder()
                .totalUsers(totalUsers)
                .activeSellers(activeSellers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .build();
    }
}
