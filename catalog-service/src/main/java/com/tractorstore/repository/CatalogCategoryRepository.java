package com.tractorstore.repository;

import com.tractorstore.model.catalog.CatalogCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatalogCategoryRepository extends JpaRepository<CatalogCategoryEntity, UUID> {}
