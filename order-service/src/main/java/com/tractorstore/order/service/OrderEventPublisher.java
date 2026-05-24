package com.tractorstore.order.service;

import com.tractorstore.order.event.InternalOrderPlacedEvent;
import com.tractorstore.order.model.OrderPlacedPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderEventPublisher {

    private final RestTemplate restTemplate;

    @Value("${order.events.order-placed.subscribers:}")
    private String subscribersConfig;

    public OrderEventPublisher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderPlaced(InternalOrderPlacedEvent event) {
        if (subscribersConfig == null || subscribersConfig.isBlank()) return;

        OrderPlacedPayload payload = new OrderPlacedPayload(
                event.getOrderId(),
                event.getSessionId(),
                event.getItems()
        );

        for (String url : subscribersConfig.split(",")) {
            String trimmedUrl = url.trim();
            if (!trimmedUrl.isEmpty()) {
                try {
                    restTemplate.postForEntity(trimmedUrl, payload, Void.class);
                } catch (Exception e) {
                    System.err.println("Failed to notify subscriber " + trimmedUrl + ": " + e.getMessage());
                }
            }
        }
    }
}
