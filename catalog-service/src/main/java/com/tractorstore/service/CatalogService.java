package com.tractorstore.service;

import com.tractorstore.data.CatalogData;
import com.tractorstore.model.HomeData;
import com.tractorstore.model.Product;
import com.tractorstore.model.Store;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private static final int MAX_RECOMMENDATIONS = 6;

    private final CatalogData catalogData;

    public CatalogService(CatalogData catalogData) {
        this.catalogData = catalogData;
    }

    public HomeData getHome() {
        var products   = catalogData.getAllProducts();
        var categories = catalogData.getAllCategories();
        var featured   = products.subList(0, Math.min(4, products.size()));
        return new HomeData(
            "Bienvenido a The Tractor Store",
            "Encuentra el tractor perfecto para tu campo",
            categories,
            featured
        );
    }

    public List<Product> getProductsByFilter(String filter) {
        var all = catalogData.getAllProducts();
        return switch (filter.toLowerCase()) {
            case "all" -> all;
            default    -> all.stream()
                             .filter(p -> p.category().equalsIgnoreCase(filter))
                             .toList();
        };
    }

    public List<Store> getStores() {
        return catalogData.getAllStores();
    }

    /**
     * Returns up to {@value #MAX_RECOMMENDATIONS} products whose colors are closest
     * (Euclidean RGB distance) to the colors of the requested SKUs.
     *
     * Algorithm (pure Java):
     *  1. Resolve the hex color of each requested SKU.
     *  2. For every other product, compute its minimum RGB distance to any reference color.
     *  3. Sort ascending by that distance and return the top results.
     *
     * @param skusCsv comma-separated SKU codes, e.g. "TRK-001,AUT-002"
     */
    public List<Product> getRecommendations(String skusCsv) {
        Set<String> requestedSkus = Arrays.stream(skusCsv.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(String::toUpperCase)
            .collect(Collectors.toUnmodifiableSet());

        Map<String, Product> skuIndex = catalogData.getAllProducts().stream()
            .collect(Collectors.toMap(p -> p.sku().toUpperCase(), p -> p));

        List<String> referenceColors = requestedSkus.stream()
            .filter(skuIndex::containsKey)
            .map(sku -> skuIndex.get(sku).color())
            .toList();

        if (referenceColors.isEmpty()) {
            return List.of();
        }

        return catalogData.getAllProducts().stream()
            .filter(p -> !requestedSkus.contains(p.sku().toUpperCase()))
            .sorted(java.util.Comparator.comparingDouble(
                p -> ColorDistanceService.minDistanceToAny(p.color(), referenceColors)
            ))
            .limit(MAX_RECOMMENDATIONS)
            .toList();
    }
}
