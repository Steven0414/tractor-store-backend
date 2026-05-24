package com.tractorstore.listener;

import com.tractorstore.event.OrderPlacedEvent;
import com.tractorstore.service.CartSessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CartEventListener {

    private final CartSessionRegistry registry;

    public CartEventListener(CartSessionRegistry registry) {
        this.registry = registry;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        registry.clear(event.getSessionId());
    }
}
