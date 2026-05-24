package com.tractorstore.listener;

import com.tractorstore.event.OrderPlacedEvent;
import com.tractorstore.repository.CartItemRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CartEventListener {

    private final CartItemRepository cartItemRepository;

    public CartEventListener(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderPlaced(OrderPlacedEvent event) {
        cartItemRepository.deleteBySessionId(event.getSessionId());
    }
}

