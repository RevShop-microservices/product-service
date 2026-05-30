package com.example.product_service.controller;

import com.example.product_service.dto.ProductRatingDTO;
import com.example.product_service.dto.ReviewRequestDTO;
import com.example.product_service.dto.ReviewResponseDTO;
import com.example.product_service.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Controller")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Add or update product review")
    public ResponseEntity<ReviewResponseDTO> addReview(@RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.ok(reviewService.addReview(dto));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get reviews for a product")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getReviews(productId));
    }

    @GetMapping("/{productId}/rating")
    @Operation(summary = "Get rating summary for a product")
    public ResponseEntity<ProductRatingDTO> getProductRating(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getProductRating(productId));
    }

    @DeleteMapping
    @Operation(summary = "Delete product review")
    public ResponseEntity<Void> deleteReview(@RequestBody ReviewRequestDTO dto) {
        reviewService.deleteReview(dto.getProductId(), dto.getUserId());
        return ResponseEntity.ok().build();
    }
}
