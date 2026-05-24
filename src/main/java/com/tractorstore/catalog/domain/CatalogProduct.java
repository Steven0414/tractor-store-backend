package com.tractorstore.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "catalog_products")
public class CatalogProduct {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String sku;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String category;

  @Column(nullable = false)
  private BigDecimal price;

  protected CatalogProduct() {
  }

  public CatalogProduct(String sku, String name, String category, BigDecimal price) {
    this.sku = sku;
    this.name = name;
    this.category = category;
    this.price = price;
  }

  public Long getId() {
    return id;
  }

  public String getSku() {
    return sku;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public BigDecimal getPrice() {
    return price;
  }
}
