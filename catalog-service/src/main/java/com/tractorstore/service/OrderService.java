package com.tractorstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.event.OrderPlacedEvent;
import com.tractorstore.model.order.PlaceOrderRequest;
import com.tractorstore.model.order.PlaceOrderResponse;
import com.tractorstore.model.outbox.OutboxEvent;
import com.tractorstore.repository.OutboxEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OutboxEventRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public OrderService(OutboxEventRepository outboxRepository,
                        ApplicationEventPublisher eventPublisher,
                        ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request, String sessionId) {
        String orderId = UUID.randomUUID().toString();
        double total = request.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        // Write Outbox entry in the same transaction
        String payload = serialize(request);
        outboxRepository.save(new OutboxEvent("Order", orderId, "OrderPlaced", payload));

        // Publish domain event (listeners fire AFTER_COMMIT)
        eventPublisher.publishEvent(new OrderPlacedEvent(this, orderId, sessionId, request.getItems()));

        return new PlaceOrderResponse(orderId, "CONFIRMED", total);
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
