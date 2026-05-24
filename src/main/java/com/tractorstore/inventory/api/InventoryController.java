package com.tractorstore.inventory.api;

import com.tractorstore.inventory.application.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping("/{sku}")
  public StockResponse stock(@PathVariable String sku) {
    return new StockResponse(sku, inventoryService.getStock(sku));
  }

  public record StockResponse(String sku, int available) {
  }
}
