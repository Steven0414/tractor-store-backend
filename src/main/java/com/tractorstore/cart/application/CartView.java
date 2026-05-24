package com.tractorstore.cart.application;

import java.math.BigDecimal;
import java.util.List;

public record CartView(String sessionId, List<Item> items, BigDecimal total) {

  public record Item(String sku, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
  }
}
