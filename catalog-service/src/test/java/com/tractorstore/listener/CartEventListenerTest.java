package com.tractorstore.listener;

import com.tractorstore.event.OrderPlacedEvent;
import com.tractorstore.repository.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEventListenerTest {

    @Mock private CartItemRepository cartItemRepository;

    @Test
    void onOrderPlaced_deletesCartItemsBySessionId() {
        CartEventListener listener = new CartEventListener(cartItemRepository);
        OrderPlacedEvent event = new OrderPlacedEvent(this, "order-1", "session-abc", List.of());

        listener.onOrderPlaced(event);

        verify(cartItemRepository).deleteBySessionId("session-abc");
    }
}
