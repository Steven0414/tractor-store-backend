package com.tractorstore.model.catalog;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "catalog_categories",
    indexes = @Index(name = "idx_catalog_categories_filter", columnList = "filter")
)
public class CatalogCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String filter;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    public CatalogCategoryEntity() {}

    public CatalogCategoryEntity(String name, String filter, String imageUrl, String description) {
        this.name = name;
        this.filter = filter;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getFilter() { return filter; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
}
