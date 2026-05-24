package com.tractorstore.notifications.service;

import com.tractorstore.notifications.model.OrderItemPayload;
import com.tractorstore.notifications.model.OrderPlacedPayload;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationServiceTest {

    private final NotificationService notificationService = new NotificationService();

    @Test
    void handleOrderPlaced_withItems_doesNotThrow() {
        OrderPlacedPayload payload = new OrderPlacedPayload(
            "ORD-001",
            "session-abc",
            List.of(
                new OrderItemPayload("SKU-1", "Tractor Wheel", 2, 49.99),
                new OrderItemPayload("SKU-2", "Engine Oil", 1, 19.99)
            )
        );

        assertDoesNotThrow(() -> notificationService.handleOrderPlaced(payload));
    }

    @Test
    void handleOrderPlaced_withNullItems_doesNotThrow() {
        OrderPlacedPayload payload = new OrderPlacedPayload("ORD-002", "session-xyz", null);

        assertDoesNotThrow(() -> notificationService.handleOrderPlaced(payload));
    }

    @Test
    void handleOrderPlaced_withEmptyItems_doesNotThrow() {
        OrderPlacedPayload payload = new OrderPlacedPayload("ORD-003", "session-def", List.of());

        assertDoesNotThrow(() -> notificationService.handleOrderPlaced(payload));
    }
}
