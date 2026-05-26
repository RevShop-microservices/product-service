package com.example.product_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDTO {

    private String productId;
    private Long userId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}