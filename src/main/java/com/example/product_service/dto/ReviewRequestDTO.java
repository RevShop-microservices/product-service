package com.example.product_service.dto;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private String productId;
    private Long userId;
    private int rating;
    private String comment;
}