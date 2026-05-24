package com.tractorstore.inventory.repository;

import com.tractorstore.inventory.model.InventoryStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryStockRepository extends JpaRepository<InventoryStockEntity, UUID> {

    Optional<InventoryStockEntity> findBySku(String sku);
}
