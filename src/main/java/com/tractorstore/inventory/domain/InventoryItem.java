package com.tractorstore.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String sku;

  @Column(nullable = false)
  private int available;

  protected InventoryItem() {
  }

  public InventoryItem(String sku, int available) {
    this.sku = sku;
    this.available = available;
  }

  public String getSku() {
    return sku;
  }

  public int getAvailable() {
    return available;
  }

  public void deduct(int quantity) {
    this.available = Math.max(0, this.available - quantity);
  }
}
