package com.tractorstore.catalog.application;

import com.tractorstore.catalog.domain.CatalogProduct;
import com.tractorstore.catalog.domain.Store;
import com.tractorstore.catalog.infrastructure.CatalogProductRepository;
import com.tractorstore.catalog.infrastructure.StoreRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

  private final CatalogProductRepository productRepository;
  private final StoreRepository storeRepository;

  public CatalogService(CatalogProductRepository productRepository, StoreRepository storeRepository) {
    this.productRepository = productRepository;
    this.storeRepository = storeRepository;
  }

  public List<CatalogProduct> getHomeTeasers() {
    return productRepository.findTop8ByOrderByIdAsc();
  }

  public List<CatalogProduct> getByCategory(String filter) {
    return productRepository.findByCategoryIgnoreCase(filter);
  }

  public CatalogProduct getProduct(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
  }

  public List<CatalogProduct> getRecommendations(List<String> skus) {
    if (skus.isEmpty()) {
      return productRepository.findTop8ByOrderByIdAsc();
    }

    CatalogProduct base = productRepository.findBySku(skus.get(0))
        .orElse(null);
    if (base == null) {
      return productRepository.findTop8ByOrderByIdAsc();
    }
    return productRepository.findByCategoryIgnoreCase(base.getCategory()).stream()
        .filter(product -> !skus.contains(product.getSku()))
        .limit(8)
        .toList();
  }

  public List<Store> getStores() {
    return storeRepository.findAll();
  }
}
