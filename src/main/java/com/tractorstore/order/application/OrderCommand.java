package com.tractorstore.order.application;

public record OrderCommand(
    String sessionId,
    String pickupStoreCode,
    String buyerName,
    String buyerEmail
) {
}
