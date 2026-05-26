package com.example.product_service.controller;

import com.example.product_service.dto.SellerAnalyticsDTO;
import com.example.product_service.service.SellerAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller/analytics")
@Tag(name = "Seller Analytics Controller", description = "APIs for seller dashboard insights")
public class SellerAnalyticsController {

    @Autowired
    private SellerAnalyticsService service;

    @GetMapping
    @Operation(
            summary = "Get Seller Analytics",
            description = "Fetch dashboard analytics including revenue, orders, stock status, and top products for a seller"
    )
    public ResponseEntity<SellerAnalyticsDTO> getAnalytics(

            @Parameter(
                    description = "Unique ID of the seller",
                    example = "101",
                    required = true
            )
            @RequestParam Long sellerId) {

        return ResponseEntity.ok(service.getSellerAnalytics(sellerId));
    }
}