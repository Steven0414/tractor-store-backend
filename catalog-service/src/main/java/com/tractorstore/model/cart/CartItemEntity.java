package com.tractorstore.model.cart;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "cart_items",
    indexes = @Index(name = "idx_cart_items_session_id", columnList = "session_id")
)
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id", nullable = false, length = 128)
    private String sessionId;

    @Column(nullable = false, length = 20)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    public CartItemEntity() {}

    public CartItemEntity(String sessionId, String sku, String name, int quantity, BigDecimal price) {
        this.sessionId = sessionId;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.addedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getSessionId() { return sessionId; }
    public String getSku() { return sku; }
    public String getName() { return name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }

    public Instant getAddedAt() { return addedAt; }
}
