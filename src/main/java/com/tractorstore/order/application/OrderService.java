package com.tractorstore.order.application;

import com.tractorstore.cart.application.CartQueryFacade;
import com.tractorstore.order.domain.OrderEntity;
import com.tractorstore.order.domain.OutboxEvent;
import com.tractorstore.order.infrastructure.OrderRepository;
import com.tractorstore.order.infrastructure.OutboxEventRepository;
import com.tractorstore.shared.events.CheckoutRequestedEvent;
import com.tractorstore.shared.events.OrderLinePayload;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OutboxEventRepository outboxEventRepository;
  private final CartQueryFacade cartQueryFacade;
  private final ApplicationEventPublisher eventPublisher;

  public OrderService(
      OrderRepository orderRepository,
      OutboxEventRepository outboxEventRepository,
      CartQueryFacade cartQueryFacade,
      ApplicationEventPublisher eventPublisher
  ) {
    this.orderRepository = orderRepository;
    this.outboxEventRepository = outboxEventRepository;
    this.cartQueryFacade = cartQueryFacade;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public OrderView placeOrder(OrderCommand command) {
    List<OrderLinePayload> lines = cartQueryFacade.getOrderLines(command.sessionId());

    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Cannot place order with empty cart");
    }

    OrderEntity order = new OrderEntity(
        command.sessionId(),
        command.pickupStoreCode(),
        command.buyerName(),
        command.buyerEmail()
    );

    for (OrderLinePayload line : lines) {
      order.addLine(line.sku(), line.quantity());
    }

    order = orderRepository.save(order);
    outboxEventRepository.save(
        new OutboxEvent(
            "ORDER",
            String.valueOf(order.getId()),
            "OrderPlaced",
            "{\"orderId\":" + order.getId() + "}"
        )
    );

    eventPublisher.publishEvent(new OrderPlacedCommitted(order.getId(), order.getSessionId(), lines));
    return toView(order);
  }

  @Transactional(readOnly = true)
  public OrderView getOrder(Long id) {
    return orderRepository.findById(id)
        .map(this::toView)
        .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
  }

  @Transactional
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onCheckoutRequested(CheckoutRequestedEvent event) {
    placeOrder(new OrderCommand(
        event.sessionId(),
        event.pickupStoreCode(),
        event.buyerName(),
        event.buyerEmail()
    ));
  }

  private OrderView toView(OrderEntity order) {
    return new OrderView(
        order.getId(),
        order.getSessionId(),
        order.getPickupStoreCode(),
        order.getBuyerName(),
        order.getBuyerEmail(),
        order.getStatus(),
        order.getCreatedAt(),
        order.getLines().stream().map(line -> new OrderView.Line(line.getSku(), line.getQuantity())).toList()
    );
  }
}
