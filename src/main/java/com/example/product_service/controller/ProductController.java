package com.example.product_service.controller;

import com.example.product_service.dto.*;
import com.example.product_service.models.Products;
import com.example.product_service.service.ImageService;
import com.example.product_service.service.ProductService;
import com.example.product_service.service.RecentlyViewedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private RecentlyViewedService recentlyViewedService;

    @Autowired
    private ProductService service;

    @Autowired
    private ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(

            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam Long sellerId) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ProductRequestDTO dto = mapper.readValue(productJson, ProductRequestDTO.class);

        List<String> imageUrls = new ArrayList<>();

        if (images != null) {
            for (MultipartFile file : images) {
                imageUrls.add(imageService.uploadImage(file));
            }
        }

        Products product = service.mapToEntity(dto, sellerId);
        product.setImages(imageUrls);

        Products saved = service.createProduct(product);

        return ResponseEntity.ok(service.mapToDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable String id,
            @RequestParam(required = false) Long userId) {

        Products product = service.getProductById(id);

        // Only add recently viewed if logged in
        if (userId != null) {
            recentlyViewedService.addViewedProduct(userId, id);
        }

        return ResponseEntity.ok(service.mapToDTO(product));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update Product")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @RequestPart("product") ProductRequestDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam Long sellerId) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                imageUrls.add(imageService.uploadImage(file));
            }
        }
        Products updated = imageService.updateProductWithImages(id, dto, sellerId, imageUrls);
        return ResponseEntity.ok(service.mapToDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Product")
    public ResponseEntity<String> deleteProduct(
            @PathVariable String id,
            @RequestParam Long sellerId) {

        log.info("API: Delete Product {}", id);

        service.deleteProduct(id, sellerId);

        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/search")
    @Operation(summary = "Search Products")
    public ResponseEntity<ProductSearchResponseDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return ResponseEntity.ok(
                service.searchProducts(
                        keyword, category, brand,
                        minPrice, maxPrice, tag,
                        page, size, sortBy, sortDir
                )
        );
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<Void> reduceStock(
            @PathVariable String id,
            @RequestParam int quantity) {
        service.reduceStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/increase-stock")
    public ResponseEntity<Void> increaseStock(
            @PathVariable String id,
            @RequestParam int quantity) {
        service.increaseStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}