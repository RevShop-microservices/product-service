package com.example.product_service.service;

import com.example.product_service.dto.SellerAnalyticsDTO;
import com.example.product_service.repository.OrderItemRepository;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class SellerAnalyticsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private SellerAnalyticsService sellerAnalyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSellerAnalytics_Success() {
        Long sellerId = 9L;

        when(productRepository.countBySellerId(sellerId)).thenReturn(25);
        when(productRepository.countBySellerIdAndStockLessThan(sellerId, 5)).thenReturn(3);
        when(orderItemRepository.getTotalOrders(sellerId)).thenReturn(50);
        when(orderItemRepository.getTotalUnitsSold(sellerId)).thenReturn(120);
        when(orderItemRepository.getTotalRevenue(sellerId)).thenReturn(2500.50);

        List<Object[]> rawTopProducts = new ArrayList<>();
        rawTopProducts.add(new Object[]{"prod-abc", "Super Laptop", 45});
        rawTopProducts.add(new Object[]{"prod-xyz", "Ergonomic Chair", 30});

        when(orderItemRepository.getTopSellingProducts(sellerId)).thenReturn(rawTopProducts);

        SellerAnalyticsDTO result = sellerAnalyticsService.getSellerAnalytics(sellerId);

        assertNotNull(result);
        assertEquals(sellerId, result.getSellerId());
        assertEquals(25, result.getTotalProducts());
        assertEquals(3, result.getLowStockProducts());
        assertEquals(50, result.getTotalOrders());
        assertEquals(120, result.getTotalUnitsSold());
        assertEquals(2500.50, result.getTotalRevenue());
        assertEquals(2, result.getTopSellingProducts().size());
        assertEquals("Super Laptop", result.getTopSellingProducts().get(0).getProductName());
        assertEquals(45, result.getTopSellingProducts().get(0).getUnitsSold());
    }

    @Test
    void testGetSellerAnalytics_NullHandling() {
        Long sellerId = 9L;

        when(productRepository.countBySellerId(sellerId)).thenReturn(0);
        when(productRepository.countBySellerIdAndStockLessThan(sellerId, 5)).thenReturn(0);
        when(orderItemRepository.getTotalOrders(sellerId)).thenReturn(null);
        when(orderItemRepository.getTotalUnitsSold(sellerId)).thenReturn(null);
        when(orderItemRepository.getTotalRevenue(sellerId)).thenReturn(null);
        when(orderItemRepository.getTopSellingProducts(sellerId)).thenReturn(new ArrayList<>());

        SellerAnalyticsDTO result = sellerAnalyticsService.getSellerAnalytics(sellerId);

        assertNotNull(result);
        assertEquals(0, result.getTotalOrders());
        assertEquals(0, result.getTotalUnitsSold());
        assertEquals(0.0, result.getTotalRevenue());
        assertEquals(0, result.getTopSellingProducts().size());
    }
}
