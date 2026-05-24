package com.tractorstore.cart.model;

import java.util.List;

public class OrderPlacedPayload {

    private String orderId;
    private String sessionId;
    private List<OrderItemPayload> items;

    public OrderPlacedPayload() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public List<OrderItemPayload> getItems() { return items; }
    public void setItems(List<OrderItemPayload> items) { this.items = items; }
}
