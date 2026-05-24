package com.tractorstore.repository;

import com.tractorstore.model.inventory.InventoryStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryStockRepository extends JpaRepository<InventoryStockEntity, UUID> {

    Optional<InventoryStockEntity> findBySku(String sku);
}
