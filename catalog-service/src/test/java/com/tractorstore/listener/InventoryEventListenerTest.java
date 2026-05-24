package com.tractorstore.listener;

import com.tractorstore.event.OrderPlacedEvent;
import com.tractorstore.model.order.OrderItem;
import com.tractorstore.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEventListenerTest {

    @Mock private InventoryService inventoryService;

    @Test
    void onOrderPlaced_callsDeductStock() {
        InventoryEventListener listener = new InventoryEventListener(inventoryService);

        OrderItem item = new OrderItem();
        item.setSku("TRK-001"); item.setName("Tractor"); item.setQuantity(2); item.setPrice(100.0);
        OrderPlacedEvent event = new OrderPlacedEvent(this, "order-1", "session-abc", List.of(item));

        listener.onOrderPlaced(event);

        verify(inventoryService).deductStock(event.getItems());
    }
}
