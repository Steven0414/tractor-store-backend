package com.tractorstore.service;

import com.tractorstore.model.inventory.InventoryStockEntity;
import com.tractorstore.model.order.OrderItem;
import com.tractorstore.repository.InventoryStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final InventoryStockRepository inventoryStockRepository;

    public InventoryService(InventoryStockRepository inventoryStockRepository) {
        this.inventoryStockRepository = inventoryStockRepository;
    }

    @Transactional(readOnly = true)
    public int getStock(String sku) {
        return inventoryStockRepository.findBySku(sku)
            .map(InventoryStockEntity::getQuantity)
            .orElse(0);
    }

    /** Deducts order quantities from stock. No-op for unknown SKUs. */
    public void deductStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            inventoryStockRepository.findBySku(item.getSku()).ifPresent(stock -> {
                stock.setQuantity(Math.max(0, stock.getQuantity() - item.getQuantity()));
                stock.setUpdatedAt(Instant.now());
                inventoryStockRepository.save(stock);
            });
        }
    }
}

