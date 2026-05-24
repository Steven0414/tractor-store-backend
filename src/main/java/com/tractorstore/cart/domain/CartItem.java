package com.tractorstore.cart.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private BigDecimal unitPrice;

  protected CartItem() {
  }

  public CartItem(Cart cart, String sku, int quantity, BigDecimal unitPrice) {
    this.cart = cart;
    this.sku = sku;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  public String getSku() {
    return sku;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }
}
