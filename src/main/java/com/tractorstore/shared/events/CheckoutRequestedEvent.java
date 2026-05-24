package com.tractorstore.shared.events;

public record CheckoutRequestedEvent(
    String sessionId,
    String pickupStoreCode,
    String buyerName,
    String buyerEmail
) {
}
