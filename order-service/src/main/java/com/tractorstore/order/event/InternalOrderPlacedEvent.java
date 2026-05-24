package com.tractorstore.order.event;

import com.tractorstore.order.model.OrderItemPayload;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class InternalOrderPlacedEvent extends ApplicationEvent {

    private final String orderId;
    private final String sessionId;
    private final List<OrderItemPayload> items;

    public InternalOrderPlacedEvent(Object source, String orderId, String sessionId, List<OrderItemPayload> items) {
        super(source);
        this.orderId = orderId;
        this.sessionId = sessionId;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getSessionId() { return sessionId; }
    public List<OrderItemPayload> getItems() { return items; }
}
