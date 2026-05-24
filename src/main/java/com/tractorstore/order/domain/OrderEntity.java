package com.tractorstore.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String sessionId;

  @Column(nullable = false)
  private String pickupStoreCode;

  @Column(nullable = false)
  private String buyerName;

  @Column(nullable = false)
  private String buyerEmail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @Column(nullable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<OrderLine> lines = new ArrayList<>();

  protected OrderEntity() {
  }

  public OrderEntity(String sessionId, String pickupStoreCode, String buyerName, String buyerEmail) {
    this.sessionId = sessionId;
    this.pickupStoreCode = pickupStoreCode;
    this.buyerName = buyerName;
    this.buyerEmail = buyerEmail;
    this.status = OrderStatus.PLACED;
    this.createdAt = Instant.now();
  }

  public void addLine(String sku, int quantity) {
    lines.add(new OrderLine(this, sku, quantity));
  }

  public Long getId() {
    return id;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getPickupStoreCode() {
    return pickupStoreCode;
  }

  public String getBuyerName() {
    return buyerName;
  }

  public String getBuyerEmail() {
    return buyerEmail;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public List<OrderLine> getLines() {
    return lines;
  }
}
