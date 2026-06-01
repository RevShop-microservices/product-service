package com.example.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsDTO {
    private long totalUsers;
    private long activeSellers;
    private long totalProducts;
    private long totalOrders;
}
