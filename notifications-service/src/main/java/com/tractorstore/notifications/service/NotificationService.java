package com.tractorstore.notifications.service;

import com.tractorstore.notifications.model.OrderItemPayload;
import com.tractorstore.notifications.model.OrderPlacedPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void handleOrderPlaced(OrderPlacedPayload payload) {
        int itemCount = payload.getItems() != null
            ? payload.getItems().stream().mapToInt(OrderItemPayload::getQuantity).sum()
            : 0;
        log.info("[EMAIL SIMULATION] Order {} confirmed — {} item(s). Email notification sent.",
            payload.getOrderId(), itemCount);
    }
}
