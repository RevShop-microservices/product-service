package com.example.product_service.dto;

import lombok.Data;
import java.util.*;

@Data
public class ProductRequestDTO {

    private String name;
    private String description;
    private String category;
    private String brand;
    private Double price;
    private Integer stock;
    private String city;
    private List<String> images;
    private List<String> tags;
    private Map<String, Object> attributes;
}