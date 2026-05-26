package com.example.product_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SellerAnalyticsDTO {

    private Long sellerId;

    private int totalProducts;
    private int totalOrders;
    private int totalUnitsSold;
    private double totalRevenue;

    private int lowStockProducts;

    private List<TopProductDTO> topSellingProducts;
}