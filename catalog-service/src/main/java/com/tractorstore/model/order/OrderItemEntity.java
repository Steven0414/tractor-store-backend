package com.tractorstore.model.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "order_items",
    indexes = @Index(name = "idx_order_items_order_id", columnList = "order_id")
)
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false, length = 20)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public OrderItemEntity() {}

    public OrderItemEntity(UUID orderId, OrderItem item) {
        this.orderId = orderId;
        this.sku = item.getSku();
        this.name = item.getName();
        this.quantity = item.getQuantity();
        this.price = BigDecimal.valueOf(item.getPrice());
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}
