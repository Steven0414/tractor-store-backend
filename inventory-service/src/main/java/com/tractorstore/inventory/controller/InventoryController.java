package com.tractorstore.inventory.controller;

import com.tractorstore.inventory.model.StockResponse;
import com.tractorstore.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{sku}")
    public ResponseEntity<StockResponse> getStock(@PathVariable String sku) {
        int quantity = inventoryService.getStock(sku);
        return ResponseEntity.ok(new StockResponse(sku, quantity));
    }
}
