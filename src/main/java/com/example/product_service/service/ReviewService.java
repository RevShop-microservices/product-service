package com.example.product_service.service;

import com.example.product_service.CustomExceptions.*;
import com.example.product_service.client.UserClient;
import com.example.product_service.dto.*;
import com.example.product_service.models.Products;
import com.example.product_service.models.Review;
import com.example.product_service.repository.OrderItemRepository;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired(required = false)
    private UserClient userClient;

    // Add or Update Review
    public ReviewResponseDTO addReview(ReviewRequestDTO dto) {

        Products product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new InvalidRequestException("Rating must be between 1 and 5");
        }

        // Verify purchase
        if (!orderItemRepository.existsByProductIdAndOrderUserId(dto.getProductId(), dto.getUserId())) {
            throw new InvalidRequestException("You can only review products you have purchased");
        }

        Review review = reviewRepository
                .findByProductIdAndUserId(dto.getProductId(), dto.getUserId())
                .orElse(Review.builder()
                        .productId(dto.getProductId())
                        .userId(dto.getUserId())
                        .createdAt(LocalDateTime.now())
                        .build());

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUpdatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);

        // Notify seller about the new review
        try {
            if (userClient != null && product.getSellerId() != null) {
                UserDTO seller = userClient.getUserById(product.getSellerId());
                if (seller != null) {
                    userClient.sendNotification(NotificationRequest.builder()
                            .userId(seller.getId())
                            .userEmail(seller.getEmail())
                            .subject("New Product Review")
                            .message("Hello " + seller.getName() + ",\n\nYour product '" + product.getName()
                                    + "' has received a new " + dto.getRating() + "-star review:\n\n"
                                    + "\"" + dto.getComment() + "\"\n\nThank you for selling on NexShop!")
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Failed to notify seller about new review", e);
        }

        return mapToDTO(saved);
    }

    // Get reviews for product
    public List<ReviewResponseDTO> getReviews(String productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Delete review
    public void deleteReview(String productId, Long userId) {
        reviewRepository.deleteByProductIdAndUserId(productId, userId);
    }

    // Get rating summary
    public ProductRatingDTO getProductRating(String productId) {

        List<Review> reviews = reviewRepository.findByProductId(productId);

        if (reviews.isEmpty()) {
            return new ProductRatingDTO(0.0, 0);
        }

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return new ProductRatingDTO(avg, reviews.size());
    }

    // Mapping
    private ReviewResponseDTO mapToDTO(Review review) {
        return ReviewResponseDTO.builder()
                .productId(review.getProductId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}