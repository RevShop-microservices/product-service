package com.example.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRatingDTO {

    private double averageRating;
    private int totalReviews;
}