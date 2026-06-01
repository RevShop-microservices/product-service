package com.example.product_service.service;

import com.example.product_service.CustomExceptions.InvalidRequestException;
import com.example.product_service.CustomExceptions.ProductNotFoundException;
import com.example.product_service.client.UserClient;
import com.example.product_service.dto.*;
import com.example.product_service.models.Products;
import com.example.product_service.models.Review;
import com.example.product_service.repository.OrderItemRepository;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── ADD REVIEW TESTS ──

    @Test
    void testAddReview_Success_NewReview() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setProductId("p1");
        dto.setUserId(1L);
        dto.setRating(5);
        dto.setComment("Amazing product!");

        Products product = Products.builder().id("p1").name("My Product").sellerId(10L).build();
        UserDTO seller = new UserDTO(10L, "Seller Nick", "seller@example.com", "SELLER");

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderItemRepository.existsByProductIdAndOrderUserId("p1", 1L)).thenReturn(true);
        when(reviewRepository.findByProductIdAndUserId("p1", 1L)).thenReturn(Optional.empty());

        Review savedReview = Review.builder()
                .productId("p1")
                .userId(1L)
                .rating(5)
                .comment("Amazing product!")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(userClient.getUserById(10L)).thenReturn(seller);

        ReviewResponseDTO result = reviewService.addReview(dto);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Amazing product!", result.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(userClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testAddReview_Success_UpdateExistingReview() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setProductId("p1");
        dto.setUserId(1L);
        dto.setRating(4);
        dto.setComment("Updated comment");

        Products product = Products.builder().id("p1").name("My Product").sellerId(10L).build();
        Review existing = Review.builder().productId("p1").userId(1L).rating(2).comment("Bad").build();

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderItemRepository.existsByProductIdAndOrderUserId("p1", 1L)).thenReturn(true);
        when(reviewRepository.findByProductIdAndUserId("p1", 1L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponseDTO result = reviewService.addReview(dto);

        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertEquals("Updated comment", result.getComment());
    }

    @Test
    void testAddReview_Fail_ProductNotFound() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setProductId("non-existent");
        dto.setUserId(1L);
        dto.setRating(5);
        dto.setComment("comment");
        when(productRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> reviewService.addReview(dto));
    }

    @Test
    void testAddReview_Fail_RatingTooLow() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setProductId("p1");
        dto.setUserId(1L);
        dto.setRating(0);
        dto.setComment("comment");
        Products product = Products.builder().id("p1").build();
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        assertThrows(InvalidRequestException.class, () -> reviewService.addReview(dto));
    }

    @Test
    void testAddReview_Fail_RatingTooHigh() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setProductId("p1");
        dto.setUserId(1L);
        dto.setRating(6);
        dto.setComment("comment");
        Products product = Products.builder().id("p1").build();
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        assertThrows(InvalidRequestException.class, () -> reviewService.addReview(dto));
    }

    @Test
    void testAddReview_Fail_ProductNotPurchased() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setProductId("p1");
        dto.setUserId(1L);
        dto.setRating(5);
        dto.setComment("comment");
        Products product = Products.builder().id("p1").build();
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderItemRepository.existsByProductIdAndOrderUserId("p1", 1L)).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> reviewService.addReview(dto));
    }

    // ── OTHER REVIEW TESTS ──

    @Test
    void testGetReviews() {
        Review r1 = Review.builder().productId("p1").userId(1L).rating(5).build();
        Review r2 = Review.builder().productId("p1").userId(2L).rating(3).build();

        when(reviewRepository.findByProductId("p1")).thenReturn(Arrays.asList(r1, r2));

        List<ReviewResponseDTO> result = reviewService.getReviews("p1");

        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getRating());
        assertEquals(3, result.get(1).getRating());
    }

    @Test
    void testDeleteReview() {
        reviewService.deleteReview("p1", 1L);
        verify(reviewRepository, times(1)).deleteByProductIdAndUserId("p1", 1L);
    }

    @Test
    void testGetProductRating_Empty() {
        when(reviewRepository.findByProductId("p1")).thenReturn(new ArrayList<>());

        ProductRatingDTO rating = reviewService.getProductRating("p1");

        assertNotNull(rating);
        assertEquals(0.0, rating.getAverageRating());
        assertEquals(0, rating.getTotalReviews());
    }

    @Test
    void testGetProductRating_WithReviews() {
        Review r1 = Review.builder().rating(5).build();
        Review r2 = Review.builder().rating(4).build();
        Review r3 = Review.builder().rating(2).build();

        when(reviewRepository.findByProductId("p1")).thenReturn(Arrays.asList(r1, r2, r3));

        ProductRatingDTO rating = reviewService.getProductRating("p1");

        assertNotNull(rating);
        assertEquals(3.6666666666666665, rating.getAverageRating(), 0.0001);
        assertEquals(3, rating.getTotalReviews());
    }
}
