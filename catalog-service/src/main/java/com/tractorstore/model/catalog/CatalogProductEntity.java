package com.tractorstore.model.catalog;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "catalog_products",
    indexes = {
        @Index(name = "idx_catalog_products_sku",      columnList = "sku"),
        @Index(name = "idx_catalog_products_category", columnList = "category")
    }
)
public class CatalogProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 7)
    private String color;

    private String motor;

    public CatalogProductEntity() {}

    public CatalogProductEntity(String sku, String name, BigDecimal price, String imageUrl,
                                 String category, String color, String motor) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.color = color;
        this.motor = motor;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getColor() { return color; }
    public String getMotor() { return motor; }
}
