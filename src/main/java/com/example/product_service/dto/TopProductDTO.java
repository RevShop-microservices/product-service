package com.example.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductDTO {

    private String productId;
    private String productName;
    private int unitsSold;
}