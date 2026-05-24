package com.tractorstore.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "order_outbox_events")
public class OutboxEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String aggregateType;

  @Column(nullable = false)
  private String aggregateId;

  @Column(nullable = false)
  private String eventType;

  @Lob
  @Column(nullable = false)
  private String payload;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private Instant createdAt;

  protected OutboxEvent() {
  }

  public OutboxEvent(String aggregateType, String aggregateId, String eventType, String payload) {
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
    this.status = "PENDING";
    this.createdAt = Instant.now();
  }

  public void markPublished() {
    this.status = "PUBLISHED";
  }

  public String getAggregateId() {
    return aggregateId;
  }
}
