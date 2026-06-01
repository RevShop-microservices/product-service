package com.example.product_service.controller;

import com.example.product_service.dto.ProductResponseDTO;
import com.example.product_service.dto.ProductSearchResponseDTO;
import com.example.product_service.models.Products;
import com.example.product_service.security.JwtUtil;
import com.example.product_service.service.ImageService;
import com.example.product_service.service.ProductService;
import com.example.product_service.service.RecentlyViewedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProductController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        }
)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private RecentlyViewedService recentlyViewedService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Test
    void testCreateProduct_Success() throws Exception {
        String productJson = "{\"name\":\"Phone\",\"price\":599.99}";
        MockMultipartFile productPart = new MockMultipartFile("product", "", "application/json", productJson.getBytes());

        Products mockProduct = Products.builder().id("p123").name("Phone").build();
        ProductResponseDTO responseDto = ProductResponseDTO.builder().id("p123").name("Phone").build();

        when(productService.mapToEntity(any(), anyLong())).thenReturn(mockProduct);
        when(productService.createProduct(any())).thenReturn(mockProduct);
        when(productService.mapToDTO(mockProduct)).thenReturn(responseDto);

        mockMvc.perform(multipart("/api/products")
                        .file(productPart)
                        .param("sellerId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    void testGetProductById_WithoutUserId() throws Exception {
        Products mockProduct = Products.builder().id("p1").name("Gadget").build();
        ProductResponseDTO responseDto = ProductResponseDTO.builder().id("p1").name("Gadget").build();

        when(productService.getProductById("p1")).thenReturn(mockProduct);
        when(productService.mapToDTO(mockProduct)).thenReturn(responseDto);

        mockMvc.perform(get("/api/products/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Gadget"));

        verifyNoInteractions(recentlyViewedService);
    }

    @Test
    void testGetProductById_WithUserId() throws Exception {
        Products mockProduct = Products.builder().id("p1").name("Gadget").build();
        ProductResponseDTO responseDto = ProductResponseDTO.builder().id("p1").name("Gadget").build();

        when(productService.getProductById("p1")).thenReturn(mockProduct);
        when(productService.mapToDTO(mockProduct)).thenReturn(responseDto);

        mockMvc.perform(get("/api/products/p1")
                        .param("userId", "99"))
                .andExpect(status().isOk());

        verify(recentlyViewedService, times(1)).addViewedProduct(99L, "p1");
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/p1")
                        .param("sellerId", "10"))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct("p1", 10L);
    }

    @Test
    void testSearchProducts() throws Exception {
        ProductSearchResponseDTO response = new ProductSearchResponseDTO(
                Collections.emptyList(), 0, 10, 0L
        );

        when(productService.searchProducts(
                eq("Laptop"), any(), any(), any(), any(), any(), eq(0), eq(10), eq("price"), eq("asc")
        )).thenReturn(response);

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void testReduceStock() throws Exception {
        mockMvc.perform(put("/api/products/p1/reduce-stock")
                        .param("quantity", "5"))
                .andExpect(status().isOk());

        verify(productService, times(1)).reduceStock("p1", 5);
    }

    @Test
    void testIncreaseStock() throws Exception {
        mockMvc.perform(put("/api/products/p1/increase-stock")
                        .param("quantity", "10"))
                .andExpect(status().isOk());

        verify(productService, times(1)).increaseStock("p1", 10);
    }
}
