package com.tractorstore.cart.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "cart_carts")
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String sessionId;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<CartItem> items = new ArrayList<>();

  protected Cart() {
  }

  public Cart(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public List<CartItem> getItems() {
    return items;
  }

  public void addOrReplaceItem(String sku, int quantity, java.math.BigDecimal unitPrice) {
    removeItem(sku);
    items.add(new CartItem(this, sku, quantity, unitPrice));
  }

  public void removeItem(String sku) {
    Iterator<CartItem> iterator = items.iterator();
    while (iterator.hasNext()) {
      CartItem item = iterator.next();
      if (item.getSku().equalsIgnoreCase(sku)) {
        iterator.remove();
      }
    }
  }

  public void clear() {
    items.clear();
  }
}
