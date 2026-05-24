package com.tractorstore.order.application;

import com.tractorstore.order.infrastructure.OutboxEventRepository;
import com.tractorstore.shared.events.OrderPlacedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderPlacedPublisher {

  private final OutboxEventRepository outboxEventRepository;
  private final ApplicationEventPublisher eventPublisher;

  public OrderPlacedPublisher(OutboxEventRepository outboxEventRepository, ApplicationEventPublisher eventPublisher) {
    this.outboxEventRepository = outboxEventRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderPlacedCommitted(OrderPlacedCommitted event) {
    outboxEventRepository.findByAggregateId(String.valueOf(event.orderId()))
        .forEach(outbox -> outbox.markPublished());

    eventPublisher.publishEvent(new OrderPlacedEvent(event.orderId(), event.sessionId(), event.lines()));
  }
}
