package com.tractorstore.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.order.event.InternalOrderPlacedEvent;
import com.tractorstore.order.model.*;
import com.tractorstore.order.repository.OrderItemRepository;
import com.tractorstore.order.repository.OrderRepository;
import com.tractorstore.order.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OutboxEventRepository outboxEventRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository, orderItemRepository, outboxEventRepository,
                eventPublisher, new ObjectMapper()
        );
    }

    private PlaceOrderRequest buildRequest() {
        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john@example.com");
        req.setPhone("555-0100");
        req.setAddress("123 Main St");
        req.setCity("Springfield");
        req.setPostalCode("12345");
        req.setPaymentMethod("CARD");
        OrderItem item = new OrderItem("TRK-001", "Tractor Rojo", 2, 12500.0);
        req.setItems(List.of(item));
        return req;
    }

    @Test
    void placeOrder_savesOrderAndPublishesEvent() {
        PlaceOrderRequest request = buildRequest();
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderItemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlaceOrderResponse response = orderService.placeOrder(request, "session-123");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getTotal()).isEqualTo(25000.0);
        assertThat(response.getOrderId()).isNotNull();

        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderItemRepository).saveAll(any());
        verify(outboxEventRepository).save(any(OutboxEvent.class));

        ArgumentCaptor<InternalOrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(InternalOrderPlacedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        InternalOrderPlacedEvent event = eventCaptor.getValue();
        assertThat(event.getSessionId()).isEqualTo("session-123");
        assertThat(event.getItems()).hasSize(1);
        assertThat(event.getItems().get(0).getSku()).isEqualTo("TRK-001");
    }

    @Test
    void getOrder_found_returnsDetail() {
        UUID id = UUID.randomUUID();
        OrderEntity entity = new OrderEntity(
                id, "Jane", "Smith", "jane@example.com", "555-0200",
                "456 Elm St", "Shelbyville", "67890", "TRANSFER",
                BigDecimal.valueOf(9999.99), "PENDING", Instant.now()
        );
        OrderItemEntity itemEntity = new OrderItemEntity(id, "TRK-002", "Mini Tractor", 1, 9999.99);

        when(orderRepository.findById(id)).thenReturn(Optional.of(entity));
        when(orderItemRepository.findByOrderId(id)).thenReturn(List.of(itemEntity));

        OrderDetailResponse detail = orderService.getOrder(id.toString());

        assertThat(detail.getOrderId()).isEqualTo(id.toString());
        assertThat(detail.getFirstName()).isEqualTo("Jane");
        assertThat(detail.getEmail()).isEqualTo("jane@example.com");
        assertThat(detail.getStatus()).isEqualTo("PENDING");
        assertThat(detail.getItems()).hasSize(1);
        assertThat(detail.getItems().get(0).getSku()).isEqualTo("TRK-002");
    }

    @Test
    void getOrder_notFound_throwsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(id.toString()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Order not found");
    }
}
