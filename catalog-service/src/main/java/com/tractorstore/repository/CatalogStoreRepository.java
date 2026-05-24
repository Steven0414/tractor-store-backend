package com.tractorstore.repository;

import com.tractorstore.model.catalog.CatalogStoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatalogStoreRepository extends JpaRepository<CatalogStoreEntity, UUID> {}
