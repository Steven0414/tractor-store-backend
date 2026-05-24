package com.tractorstore.repository;

import com.tractorstore.model.catalog.CatalogProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogProductRepository extends JpaRepository<CatalogProductEntity, UUID> {

    List<CatalogProductEntity> findByCategory(String category);

    Optional<CatalogProductEntity> findBySku(String sku);
}
