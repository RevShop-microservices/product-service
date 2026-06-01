package com.example.product_service.service;

import com.example.product_service.models.Products;
import com.example.product_service.models.RecentlyViewed;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.RecentlyViewedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecentlyViewedServiceTest {

    @Mock
    private RecentlyViewedRepository repository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private RecentlyViewedService recentlyViewedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddViewedProduct_NewUser() {
        Long userId = 1L;
        String productId = "prod-100";

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        recentlyViewedService.addViewedProduct(userId, productId);

        verify(repository, times(1)).save(argThat(rv -> {
            assertEquals(userId, rv.getUserId());
            assertEquals(1, rv.getProductIds().size());
            assertEquals("prod-100", rv.getProductIds().getFirst());
            return true;
        }));
    }

    @Test
    void testAddViewedProduct_ExistingUser_UnderLimit() {
        Long userId = 1L;
        String productId = "prod-200";

        LinkedList<String> existingIds = new LinkedList<>(Arrays.asList("prod-100", "prod-300"));
        RecentlyViewed existingRv = RecentlyViewed.builder()
                .userId(userId)
                .productIds(existingIds)
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existingRv));

        recentlyViewedService.addViewedProduct(userId, productId);

        verify(repository, times(1)).save(argThat(rv -> {
            assertEquals(3, rv.getProductIds().size());
            assertEquals("prod-200", rv.getProductIds().getFirst());
            return true;
        }));
    }

    @Test
    void testAddViewedProduct_ExistingUser_AlreadyExists_MovesToHead() {
        Long userId = 1L;
        String productId = "prod-100";

        LinkedList<String> existingIds = new LinkedList<>(Arrays.asList("prod-100", "prod-300"));
        RecentlyViewed existingRv = RecentlyViewed.builder()
                .userId(userId)
                .productIds(existingIds)
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existingRv));

        recentlyViewedService.addViewedProduct(userId, productId);

        verify(repository, times(1)).save(argThat(rv -> {
            assertEquals(2, rv.getProductIds().size());
            assertEquals("prod-100", rv.getProductIds().getFirst());
            assertEquals("prod-300", rv.getProductIds().get(1));
            return true;
        }));
    }

    @Test
    void testAddViewedProduct_ExceedsLimit() {
        Long userId = 1L;
        String productId = "prod-new";

        LinkedList<String> existingIds = new LinkedList<>();
        for (int i = 1; i <= 10; i++) {
            existingIds.add("prod-" + i);
        }
        RecentlyViewed existingRv = RecentlyViewed.builder()
                .userId(userId)
                .productIds(existingIds)
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existingRv));

        recentlyViewedService.addViewedProduct(userId, productId);

        verify(repository, times(1)).save(argThat(rv -> {
            assertEquals(10, rv.getProductIds().size());
            assertEquals("prod-new", rv.getProductIds().getFirst());
            assertFalse(rv.getProductIds().contains("prod-10")); // Last item removed
            return true;
        }));
    }

    @Test
    void testGetRecentlyViewed_Success() {
        Long userId = 1L;
        List<String> productIds = Arrays.asList("p1", "p2");
        RecentlyViewed rv = RecentlyViewed.builder().userId(userId).productIds(new LinkedList<>(productIds)).build();

        Products prod1 = Products.builder().id("p1").name("Product 1").build();
        Products prod2 = Products.builder().id("p2").name("Product 2").build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(rv));
        when(productRepository.findAllById(rv.getProductIds())).thenReturn(Arrays.asList(prod1, prod2));

        List<Products> result = recentlyViewedService.getRecentlyViewed(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("p1", result.get(0).getId());
        assertEquals("p2", result.get(1).getId());
    }

    @Test
    void testGetRecentlyViewed_NoHistory() {
        Long userId = 1L;
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> recentlyViewedService.getRecentlyViewed(userId));
    }
}
