package com.tractorstore.controller;

import com.tractorstore.model.HomeData;
import com.tractorstore.model.Product;
import com.tractorstore.model.Store;
import com.tractorstore.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/home")
    public ResponseEntity<HomeData> getHome() {
        return ResponseEntity.ok(catalogService.getHome());
    }

    @GetMapping("/products/{sku}")
    public ResponseEntity<Product> getProduct(@PathVariable String sku) {
        return ResponseEntity.ok(catalogService.getProductBySku(sku));
    }

    @GetMapping("/categories/{filter}")
    public ResponseEntity<List<Product>> getCategories(@PathVariable String filter) {
        return ResponseEntity.ok(catalogService.getProductsByFilter(filter));
    }

    @GetMapping("/stores")
    public ResponseEntity<List<Store>> getStores() {
        return ResponseEntity.ok(catalogService.getStores());
    }

    /**
     * GET /api/catalog/recommendations?skus={csv}
     *
     * Returns up to 6 products whose colors are most similar (smallest Euclidean
     * RGB distance) to the colors of the provided SKUs.
     *
     * @param skus comma-separated SKU codes, e.g. "TRK-001,AUT-002"
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<Product>> getRecommendations(
            @RequestParam(name = "skus") String skus) {
        List<Product> recommendations = catalogService.getRecommendations(skus);
        return ResponseEntity.ok(recommendations);
    }
}
