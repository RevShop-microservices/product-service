package com.example.product_service.controller;

import com.example.product_service.dto.AdminAnalyticsDTO;
import com.example.product_service.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
@Tag(name = "Admin Analytics Controller", description = "APIs for admin dashboard insights")
public class AdminAnalyticsController {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @GetMapping
    @Operation(
            summary = "Get Admin Analytics",
            description = "Fetch admin dashboard metrics including total users, active sellers, total products, and total orders"
    )
    public ResponseEntity<AdminAnalyticsDTO> getAdminAnalytics() {
        return ResponseEntity.ok(adminAnalyticsService.getAdminAnalytics());
    }
}
