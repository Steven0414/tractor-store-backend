package com.tractorstore.cart.application;

import com.tractorstore.shared.events.OrderLinePayload;
import java.util.List;

public interface CartQueryFacade {

  List<OrderLinePayload> getOrderLines(String sessionId);

  void clearCart(String sessionId);
}
