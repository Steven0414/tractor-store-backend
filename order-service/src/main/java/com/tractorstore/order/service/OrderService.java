package com.tractorstore.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.order.event.InternalOrderPlacedEvent;
import com.tractorstore.order.model.*;
import com.tractorstore.order.repository.OrderItemRepository;
import com.tractorstore.order.repository.OrderRepository;
import com.tractorstore.order.repository.OutboxEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OutboxEventRepository outboxEventRepository,
                        ApplicationEventPublisher eventPublisher,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request, String sessionId) {
        UUID orderId = UUID.randomUUID();

        BigDecimal total = request.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity(
                orderId,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getAddress(),
                request.getCity(),
                request.getPostalCode(),
                request.getPaymentMethod(),
                total,
                "PENDING",
                Instant.now()
        );
        orderRepository.save(order);

        List<OrderItemEntity> itemEntities = request.getItems().stream()
                .map(item -> new OrderItemEntity(orderId, item.getSku(), item.getName(), item.getQuantity(), item.getPrice()))
                .toList();
        orderItemRepository.saveAll(itemEntities);

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            payloadJson = "{}";
        }
        outboxEventRepository.save(new OutboxEvent("order", orderId.toString(), "ORDER_PLACED", payloadJson));

        eventPublisher.publishEvent(new InternalOrderPlacedEvent(this, orderId.toString(), sessionId, toPayloads(request.getItems())));

        return new PlaceOrderResponse(orderId.toString(), "PENDING", total.doubleValue());
    }

    public OrderDetailResponse getOrder(String id) {
        UUID uuid = UUID.fromString(id);
        OrderEntity order = orderRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));

        List<OrderItemDetail> itemDetails = orderItemRepository.findByOrderId(uuid).stream()
                .map(item -> new OrderItemDetail(item.getSku(), item.getName(), item.getQuantity(), item.getPrice().doubleValue()))
                .toList();

        return new OrderDetailResponse(
                order.getId().toString(),
                order.getFirstName(),
                order.getLastName(),
                order.getEmail(),
                order.getStatus(),
                order.getTotal().doubleValue(),
                order.getCreatedAt(),
                itemDetails
        );
    }

    private List<OrderItemPayload> toPayloads(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemPayload(item.getSku(), item.getName(), item.getQuantity(), item.getPrice()))
                .toList();
    }
}
