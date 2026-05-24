package com.tractorstore.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items", indexes = @Index(name = "idx_order_items_order_id", columnList = "order_id"))
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    private String sku;
    private String name;
    private int quantity;
    private BigDecimal price;

    public OrderItemEntity() {}

    public OrderItemEntity(UUID orderId, String sku, String name, int quantity, double price) {
        this.orderId = orderId;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.price = BigDecimal.valueOf(price);
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}
