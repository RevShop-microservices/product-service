package com.example.product_service.service;

import com.example.product_service.CustomExceptions.InvalidFilterException;
import com.example.product_service.CustomExceptions.InvalidRequestException;
import com.example.product_service.CustomExceptions.ProductNotFoundException;
import com.example.product_service.CustomExceptions.UnauthorizedException;
import com.example.product_service.dto.*;
import com.example.product_service.models.Products;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private MongoOperations mongoTemplate;

    @Mock
    private EmbeddingService embeddingService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── MAPPING TESTS ──

    @Test
    void testMapToDTO() {
        Products prod = Products.builder()
                .id("p1")
                .name("Laptop")
                .description("Good")
                .category("Electronics")
                .brand("BrandA")
                .price(1000.0)
                .stock(10)
                .city("Chicago")
                .images(Arrays.asList("img1.png"))
                .tags(Arrays.asList("pc"))
                .attributes(Map.of("RAM", "16GB"))
                .build();

        ProductResponseDTO dto = productService.mapToDTO(prod);

        assertNotNull(dto);
        assertEquals("p1", dto.getId());
        assertEquals("Laptop", dto.getName());
        assertTrue(dto.isInStock());
        assertEquals("16GB", dto.getAttributes().get("RAM"));
    }

    @Test
    void testMapToEntity() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Phone");
        dto.setDescription("Desc");
        dto.setPrice(500.0);
        dto.setCategory("Mobile");
        dto.setBrand("BrandB");
        dto.setStock(5);
        dto.setAttributes(Map.of("color", "red"));
        dto.setImages(Arrays.asList("img2.png"));
        dto.setTags(Arrays.asList("tag2"));
        dto.setCity("New York");

        Products entity = productService.mapToEntity(dto, 99L);

        assertNotNull(entity);
        assertEquals("Phone", entity.getName());
        assertEquals(99L, entity.getSellerId());
    }

    // ── CREATE / GET TESTS ──

    @Test
    void testCreateProduct() {
        Products prod = Products.builder().name("T-shirt").description("cotton").build();
        List<Double> embedding = Arrays.asList(0.1, 0.2, 0.3);

        when(embeddingService.getEmbedding("T-shirt cotton")).thenReturn(embedding);
        when(repository.save(prod)).thenReturn(prod);

        Products saved = productService.createProduct(prod);

        assertNotNull(saved);
        assertEquals(embedding, saved.getEmbedding());
        verify(repository, times(1)).save(prod);
    }

    @Test
    void testGetProductById_RepoSuccess() {
        Products prod = Products.builder().id("p123").build();
        when(repository.findById("p123")).thenReturn(Optional.of(prod));

        Products result = productService.getProductById("p123");

        assertNotNull(result);
        assertEquals("p123", result.getId());
    }

    @Test
    void testGetProductById_NumericFallback_Success() {
        when(repository.findById("123")).thenReturn(Optional.empty());

        Products prod = Products.builder().id("123").name("Numeric product").build();
        when(mongoTemplate.findById(123, Products.class)).thenReturn(prod);

        Products result = productService.getProductById("123");

        assertNotNull(result);
        assertEquals("123", result.getId());
    }

    @Test
    void testGetProductById_NotFound() {
        when(repository.findById("p1")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById("p1"));
    }

    // ── UPDATE / DELETE TESTS ──

    @Test
    void testUpdateProduct_Success() {
        Products existing = Products.builder().id("p1").sellerId(10L).name("Old name").description("Old desc").build();
        ProductRequestDTO req = new ProductRequestDTO();
        req.setName("New name");
        req.setPrice(150.0);

        when(repository.findById("p1")).thenReturn(Optional.of(existing));
        when(embeddingService.getEmbedding(anyString())).thenReturn(Arrays.asList(0.1, 0.2));
        when(repository.save(any(Products.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Products result = productService.updateProduct("p1", req, 10L);

        assertNotNull(result);
        assertEquals("New name", result.getName());
        assertEquals("Old desc", result.getDescription()); // unchanged
        assertEquals(150.0, result.getPrice());
    }

    @Test
    void testUpdateProduct_Unauthorized() {
        Products existing = Products.builder().id("p1").sellerId(10L).build();
        ProductRequestDTO req = new ProductRequestDTO();

        when(repository.findById("p1")).thenReturn(Optional.of(existing));

        assertThrows(UnauthorizedException.class, () -> productService.updateProduct("p1", req, 99L));
    }

    @Test
    void testDeleteProduct_Success() {
        Products existing = Products.builder().id("p1").sellerId(10L).build();
        when(repository.findById("p1")).thenReturn(Optional.of(existing));

        productService.deleteProduct("p1", 10L);

        verify(repository, times(1)).deleteById("p1");
    }

    @Test
    void testDeleteProduct_Unauthorized() {
        Products existing = Products.builder().id("p1").sellerId(10L).build();
        when(repository.findById("p1")).thenReturn(Optional.of(existing));

        assertThrows(UnauthorizedException.class, () -> productService.deleteProduct("p1", 99L));
    }

    // ── STOCK MANAGEMENT TESTS ──

    @Test
    void testReduceStock_Success() {
        Products prod = Products.builder().id("p1").stock(10).build();
        when(repository.findById("p1")).thenReturn(Optional.of(prod));

        productService.reduceStock("p1", 4);

        assertEquals(6, prod.getStock());
        verify(repository, times(1)).save(prod);
    }

    @Test
    void testReduceStock_Insufficient() {
        Products prod = Products.builder().id("p1").stock(3).build();
        when(repository.findById("p1")).thenReturn(Optional.of(prod));

        assertThrows(InvalidRequestException.class, () -> productService.reduceStock("p1", 5));
    }

    @Test
    void testIncreaseStock_Success() {
        Products prod = Products.builder().id("p1").stock(10).build();
        when(repository.findById("p1")).thenReturn(Optional.of(prod));

        productService.increaseStock("p1", 5);

        assertEquals(15, prod.getStock());
        verify(repository, times(1)).save(prod);
    }

    // ── SEARCH TESTS ──

    @Test
    void testSearchProducts_Success() {
        Products prod = Products.builder().id("p1").name("Matching Laptop").price(100.0).build();

        when(mongoTemplate.count(any(Query.class), eq(Products.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Products.class))).thenReturn(Arrays.asList(prod));

        ProductSearchResponseDTO result = productService.searchProducts(
                "Laptop", "Electronics", "Brand", 50.0, 150.0, "tag",
                0, 10, "price", "asc"
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getProducts().size());
        assertEquals("Matching Laptop", result.getProducts().get(0).getName());
    }

    @Test
    void testSearchProducts_InvalidPriceRange() {
        assertThrows(InvalidFilterException.class, () -> productService.searchProducts(
                null, null, null, 200.0, 100.0, null, 0, 10, null, null
        ));
    }
}
