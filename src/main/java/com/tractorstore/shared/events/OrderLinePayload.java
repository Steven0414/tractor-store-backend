package com.tractorstore.shared.events;

public record OrderLinePayload(String sku, int quantity) {
}
