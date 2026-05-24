package com.tractorstore.event;

import com.tractorstore.model.order.OrderItem;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class OrderPlacedEvent extends ApplicationEvent {

    private final String orderId;
    private final String sessionId;
    private final List<OrderItem> items;

    public OrderPlacedEvent(Object source, String orderId, String sessionId, List<OrderItem> items) {
        super(source);
        this.orderId = orderId;
        this.sessionId = sessionId;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getSessionId() { return sessionId; }
    public List<OrderItem> getItems() { return items; }
}
