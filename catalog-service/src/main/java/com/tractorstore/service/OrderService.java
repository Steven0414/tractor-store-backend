package com.tractorstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.event.OrderPlacedEvent;
import com.tractorstore.model.order.OrderEntity;
import com.tractorstore.model.order.OrderItemEntity;
import com.tractorstore.model.order.PlaceOrderRequest;
import com.tractorstore.model.order.PlaceOrderResponse;
import com.tractorstore.model.outbox.OutboxEvent;
import com.tractorstore.repository.OrderItemRepository;
import com.tractorstore.repository.OrderRepository;
import com.tractorstore.repository.OutboxEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OutboxEventRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OutboxEventRepository outboxRepository,
                        ApplicationEventPublisher eventPublisher,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request, String sessionId) {
        UUID orderId = UUID.randomUUID();
        BigDecimal total = request.getItems().stream()
            .map(i -> BigDecimal.valueOf(i.getPrice()).multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Persist order and items in the same transaction
        OrderEntity order = new OrderEntity(
            orderId,
            request.getFirstName(), request.getLastName(), request.getEmail(),
            request.getPhone(), request.getAddress(), request.getCity(),
            request.getPostalCode(), request.getPaymentMethod(),
            total, "CONFIRMED"
        );
        orderRepository.save(order);

        List<OrderItemEntity> items = request.getItems().stream()
            .map(item -> new OrderItemEntity(orderId, item))
            .toList();
        orderItemRepository.saveAll(items);

        // Write Outbox entry in the same transaction (guarantees at-least-once delivery)
        String payload = serialize(request);
        outboxRepository.save(new OutboxEvent("Order", orderId.toString(), "OrderPlaced", payload));

        // Publish domain event (listeners fire AFTER_COMMIT)
        eventPublisher.publishEvent(new OrderPlacedEvent(this, orderId.toString(), sessionId, request.getItems()));

        return new PlaceOrderResponse(orderId.toString(), "CONFIRMED", total.doubleValue());
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

