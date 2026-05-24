package com.tractorstore.order.application;

import com.tractorstore.order.domain.OrderStatus;
import java.time.Instant;
import java.util.List;

public record OrderView(
    Long id,
    String sessionId,
    String pickupStoreCode,
    String buyerName,
    String buyerEmail,
    OrderStatus status,
    Instant createdAt,
    List<Line> lines
) {

  public record Line(String sku, int quantity) {
  }
}
