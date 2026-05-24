package com.tractorstore.order.application;

import com.tractorstore.shared.events.OrderLinePayload;
import java.util.List;

public record OrderPlacedCommitted(Long orderId, String sessionId, List<OrderLinePayload> lines) {
}
