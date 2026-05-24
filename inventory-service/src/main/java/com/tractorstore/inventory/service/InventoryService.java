package com.tractorstore.inventory.service;

import com.tractorstore.inventory.model.InventoryStockEntity;
import com.tractorstore.inventory.model.OrderItemPayload;
import com.tractorstore.inventory.repository.InventoryStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

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
                .orElseThrow(() -> new NoSuchElementException("SKU not found: " + sku));
    }

    /** Deducts order quantities from stock. No-op for unknown SKUs. */
    public void deductStock(List<OrderItemPayload> items) {
        for (OrderItemPayload item : items) {
            inventoryStockRepository.findBySku(item.getSku()).ifPresent(stock -> {
                stock.setQuantity(Math.max(0, stock.getQuantity() - item.getQuantity()));
                stock.setUpdatedAt(Instant.now());
                inventoryStockRepository.save(stock);
            });
        }
    }
}
