package com.tractorstore.inventory.infrastructure;

import com.tractorstore.inventory.domain.InventoryItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

  Optional<InventoryItem> findBySku(String sku);
}
