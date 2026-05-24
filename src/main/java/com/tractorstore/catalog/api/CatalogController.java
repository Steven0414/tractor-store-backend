package com.tractorstore.catalog.api;

import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.CatalogProduct;
import com.tractorstore.catalog.domain.Store;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

  private final CatalogService catalogService;

  public CatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/home")
  public List<CatalogProduct> home() {
    return catalogService.getHomeTeasers();
  }

  @GetMapping("/categories/{filter}")
  public List<CatalogProduct> byCategory(@PathVariable String filter) {
    return catalogService.getByCategory(filter);
  }

  @GetMapping("/products/{id}")
  public CatalogProduct productDetail(@PathVariable Long id) {
    return catalogService.getProduct(id);
  }

  @GetMapping("/recommendations")
  public List<CatalogProduct> recommendations(@RequestParam(name = "skus", defaultValue = "") String skus) {
    List<String> skuList = skus.isBlank() ? List.of() : Arrays.asList(skus.split(","));
    return catalogService.getRecommendations(skuList.stream().map(String::trim).toList());
  }

  @GetMapping("/stores")
  public List<Store> stores() {
    return catalogService.getStores();
  }
}
