package com.tractorstore.inventory.application;

import com.tractorstore.inventory.domain.InventoryItem;
import com.tractorstore.inventory.infrastructure.InventoryRepository;
import com.tractorstore.shared.events.OrderPlacedEvent;
import com.tractorstore.shared.events.OrderLinePayload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class InventoryService {

  private final InventoryRepository inventoryRepository;

  public InventoryService(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  @Transactional(readOnly = true)
  public int getStock(String sku) {
    return inventoryRepository.findBySku(sku).map(InventoryItem::getAvailable).orElse(0);
  }

  @Transactional
  @TransactionalEventListener
  public void onOrderPlaced(OrderPlacedEvent event) {
    for (OrderLinePayload line : event.lines()) {
      inventoryRepository.findBySku(line.sku()).ifPresent(item -> item.deduct(line.quantity()));
    }
  }
}
