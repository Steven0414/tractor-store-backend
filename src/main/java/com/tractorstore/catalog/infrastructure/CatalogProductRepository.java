package com.tractorstore.catalog.infrastructure;

import com.tractorstore.catalog.domain.CatalogProduct;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogProductRepository extends JpaRepository<CatalogProduct, Long> {

  List<CatalogProduct> findByCategoryIgnoreCase(String category);

  Optional<CatalogProduct> findBySku(String sku);

  List<CatalogProduct> findTop8ByOrderByIdAsc();
}
