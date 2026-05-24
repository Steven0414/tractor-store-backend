package com.tractorstore.order.model;

import java.util.List;

public class OrderPlacedPayload {

    private String orderId;
    private String sessionId;
    private List<OrderItemPayload> items;

    public OrderPlacedPayload() {}

    public OrderPlacedPayload(String orderId, String sessionId, List<OrderItemPayload> items) {
        this.orderId = orderId;
        this.sessionId = sessionId;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getSessionId() { return sessionId; }
    public List<OrderItemPayload> getItems() { return items; }
}
