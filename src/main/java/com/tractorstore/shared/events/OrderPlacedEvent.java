package com.tractorstore.shared.events;

import java.util.List;

public record OrderPlacedEvent(Long orderId, String sessionId, List<OrderLinePayload> lines) {
}
