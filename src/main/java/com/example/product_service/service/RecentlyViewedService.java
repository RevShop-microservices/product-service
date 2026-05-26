package com.example.product_service.service;

import com.example.product_service.models.*;
import com.example.product_service.repository.*;
//import com.Revature.Ecommerce.Platform.repository.RecentlyViewedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecentlyViewedService {

    private static final int MAX_SIZE=10;

    @Autowired
    private RecentlyViewedRepository repository;

    @Autowired
    private ProductRepository productRepository;

    //adding product to recently viewed products
    public void addViewedProduct(Long userId, String productId) {
        RecentlyViewed rv = repository.findByUserId(userId)
                .orElse(RecentlyViewed.builder()
                        .userId(userId)
                        .productIds(new LinkedList<>())
                        .build());

        LinkedList<String> list = rv.getProductIds();
        list.remove(productId);

        list.addFirst(productId);

        if(list.size()>MAX_SIZE){
            list.removeLast();
        }
        repository.save(rv);
    }

    //viewing recently viewed products
    public List<Products> getRecentlyViewed(Long userId) {
        RecentlyViewed rv = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No recently viewed products"));
        List<Products> products=productRepository.findAllById(rv.getProductIds());
        Map<String, Products> productMap = products.stream()
                .collect(Collectors.toMap(
                        Products::getId,
                        product -> product
                ));
        return rv.getProductIds().stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}