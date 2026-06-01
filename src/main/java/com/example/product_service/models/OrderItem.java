package com.example.product_service.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;

@Entity
@Table(name = "order_items1")
@Access(AccessType.FIELD)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Long sellerId;
    private String city;
    private boolean isCancelled;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    // Constructors
    public OrderItem() {}

    public OrderItem(Long orderItemId, String productId, String productName, Integer quantity, Double price, Long sellerId, String city, boolean isCancelled, Order order) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.sellerId = sellerId;
        this.city = city;
        this.isCancelled = isCancelled;
        this.order = order;
    }

    // Getters and Setters
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Builder Implementation
    public static OrderItemBuilder builder() {
        return new OrderItemBuilder();
    }

    public static class OrderItemBuilder {
        private Long orderItemId;
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
        private Long sellerId;
        private String city;
        private boolean isCancelled;
        private Order order;

        OrderItemBuilder() {}

        public OrderItemBuilder orderItemId(Long orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }

        public OrderItemBuilder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public OrderItemBuilder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public OrderItemBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItemBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public OrderItemBuilder sellerId(Long sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public OrderItemBuilder city(String city) {
            this.city = city;
            return this;
        }

        public OrderItemBuilder isCancelled(boolean isCancelled) {
            this.isCancelled = isCancelled;
            return this;
        }

        public OrderItemBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(orderItemId, productId, productName, quantity, price, sellerId, city, isCancelled, order);
        }
    }
}