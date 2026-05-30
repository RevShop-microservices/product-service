package com.example.product_service.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;
    private Double subtotal;
    private Double discount;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;
    private Long addressId;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items;

    // Constructors
    public Order() {}

    public Order(Long orderId, Long userId, Double subtotal, Double discount, Double totalAmount,
                 OrderStatus status, LocalDateTime orderDate, Long addressId, List<OrderItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.subtotal = subtotal;
        this.discount = discount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.addressId = addressId;
        this.items = items;
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    // Builder
    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public static class OrderBuilder {
        private Long orderId;
        private Long userId;
        private Double subtotal;
        private Double discount;
        private Double totalAmount;
        private OrderStatus status;
        private LocalDateTime orderDate;
        private Long addressId;
        private List<OrderItem> items;

        OrderBuilder() {}

        public OrderBuilder orderId(Long orderId) { this.orderId = orderId; return this; }
        public OrderBuilder userId(Long userId) { this.userId = userId; return this; }
        public OrderBuilder subtotal(Double subtotal) { this.subtotal = subtotal; return this; }
        public OrderBuilder discount(Double discount) { this.discount = discount; return this; }
        public OrderBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderBuilder orderDate(LocalDateTime orderDate) { this.orderDate = orderDate; return this; }
        public OrderBuilder addressId(Long addressId) { this.addressId = addressId; return this; }
        public OrderBuilder items(List<OrderItem> items) { this.items = items; return this; }

        public Order build() {
            return new Order(orderId, userId, subtotal, discount, totalAmount, status, orderDate, addressId, items);
        }
    }
}