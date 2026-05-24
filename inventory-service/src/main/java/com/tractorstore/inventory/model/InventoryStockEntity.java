package com.tractorstore.inventory.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "inventory_stock",
    indexes = @Index(name = "idx_inventory_stock_sku", columnList = "sku", unique = true)
)
public class InventoryStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public InventoryStockEntity() {}

    public InventoryStockEntity(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
