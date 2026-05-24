package com.tractorstore.inventory.controller;

import com.tractorstore.inventory.model.OrderPlacedPayload;
import com.tractorstore.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/events")
public class EventController {

    private final InventoryService inventoryService;

    public EventController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/order-placed")
    public ResponseEntity<Void> handleOrderPlaced(@RequestBody OrderPlacedPayload payload) {
        inventoryService.deductStock(payload.getItems());
        return ResponseEntity.ok().build();
    }
}
