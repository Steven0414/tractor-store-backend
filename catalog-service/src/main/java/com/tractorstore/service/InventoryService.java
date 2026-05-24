package com.tractorstore.service;

import com.tractorstore.model.order.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory inventory.
 * In a real system this would delegate to a DB or a dedicated service.
 */
@Service
public class InventoryService {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    /** Seed stock (called from CatalogData or tests). */
    public void setStock(String sku, int quantity) {
        stock.put(sku, quantity);
    }

    public int getStock(String sku) {
        return stock.getOrDefault(sku, 0);
    }

    /** Deducts order quantities from stock. No-op for unknown SKUs. */
    public void deductStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            stock.merge(item.getSku(), item.getQuantity(), (current, qty) -> Math.max(0, current - qty));
        }
    }
}
