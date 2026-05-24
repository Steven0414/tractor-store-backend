package com.tractorstore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.event.OrderPlacedEvent;
import com.tractorstore.model.order.OrderEntity;
import com.tractorstore.model.order.OrderItem;
import com.tractorstore.model.order.PlaceOrderRequest;
import com.tractorstore.model.order.PlaceOrderResponse;
import com.tractorstore.model.outbox.OutboxEvent;
import com.tractorstore.repository.OrderItemRepository;
import com.tractorstore.repository.OrderRepository;
import com.tractorstore.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OutboxEventRepository outboxRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, orderItemRepository,
                outboxRepository, eventPublisher, new ObjectMapper());
    }

    private PlaceOrderRequest validRequest() {
        OrderItem item = new OrderItem();
        item.setSku("TRK-001"); item.setName("Tractor"); item.setQuantity(2); item.setPrice(5000.0);

        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setFirstName("John"); req.setLastName("Doe"); req.setEmail("john@test.com");
        req.setPhone("555-1234"); req.setAddress("Main St 1"); req.setCity("Lima");
        req.setPostalCode("15001"); req.setPaymentMethod("CARD");
        req.setItems(List.of(item));
        return req;
    }

    @Test
    void placeOrder_persistsOrderAndItems() {
        PlaceOrderResponse response = orderService.placeOrder(validRequest(), "session-123");

        assertNotNull(response.getOrderId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals(10000.0, response.getTotal(), 0.001);
        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderItemRepository).saveAll(any());
        verify(outboxRepository).save(any(OutboxEvent.class));
    }

    @Test
    void placeOrder_publishesOrderPlacedEvent() {
        orderService.placeOrder(validRequest(), "session-456");

        ArgumentCaptor<OrderPlacedEvent> captor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals("session-456", captor.getValue().getSessionId());
    }

    @Test
    void placeOrder_multipleItems_sumsTotal() {
        OrderItem item1 = new OrderItem();
        item1.setSku("A"); item1.setName("A"); item1.setQuantity(1); item1.setPrice(1000.0);
        OrderItem item2 = new OrderItem();
        item2.setSku("B"); item2.setName("B"); item2.setQuantity(3); item2.setPrice(200.0);

        PlaceOrderRequest req = validRequest();
        req.setItems(List.of(item1, item2));

        PlaceOrderResponse resp = orderService.placeOrder(req, "s");

        assertEquals(1600.0, resp.getTotal(), 0.001);
    }

    @Test
    void placeOrder_outboxPayloadContainsOrderData() {
        orderService.placeOrder(validRequest(), "sess");

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(captor.capture());
        assertEquals("Order", captor.getValue().getAggregateType());
        assertEquals("OrderPlaced", captor.getValue().getEventType());
    }
}
