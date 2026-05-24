package com.tractorstore.service;

import com.tractorstore.data.CatalogData;
import com.tractorstore.model.Category;
import com.tractorstore.model.HomeData;
import com.tractorstore.model.Product;
import com.tractorstore.model.Store;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
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
        List<Product> allProducts = catalogData.getAllProducts();
        List<Category> categories = catalogData.getAllCategories();
        List<Product> featured = allProducts.subList(0, Math.min(4, allProducts.size()));
        return new HomeData(
            "Bienvenido a The Tractor Store",
            "Encuentra el tractor perfecto para tu campo",
            categories,
            featured
        );
    }

    public List<Product> getProductsByFilter(String filter) {
        List<Product> all = catalogData.getAllProducts();
        if ("all".equalsIgnoreCase(filter)) {
            return all;
        }
        return all.stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(filter))
            .collect(Collectors.toList());
    }

    public List<Store> getStores() {
        return catalogData.getAllStores();
    }

    /**
     * Returns up to {@value #MAX_RECOMMENDATIONS} products whose colors are closest
     * (Euclidean distance in RGB space) to the colors of the requested SKUs.
     *
     * <p>Algorithm (pure Java):
     * <ol>
     *   <li>Resolve the hex colors of all requested SKUs.</li>
     *   <li>For every other product compute its minimum RGB distance to any reference color.</li>
     *   <li>Sort candidates ascending by that distance.</li>
     *   <li>Return the top {@value #MAX_RECOMMENDATIONS} results.</li>
     * </ol>
     *
     * @param skusCsv comma-separated list of SKU codes
     * @return ordered list of recommended products (closest color first)
     */
    public List<Product> getRecommendations(String skusCsv) {
        Set<String> requestedSkus = Arrays.stream(skusCsv.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(String::toUpperCase)
            .collect(Collectors.toSet());

        Map<String, Product> skuToProduct = catalogData.getAllProducts().stream()
            .collect(Collectors.toMap(p -> p.getSku().toUpperCase(), p -> p));

        List<String> referenceColors = requestedSkus.stream()
            .filter(skuToProduct::containsKey)
            .map(sku -> skuToProduct.get(sku).getColor())
            .collect(Collectors.toList());

        if (referenceColors.isEmpty()) {
            return List.of();
        }

        return catalogData.getAllProducts().stream()
            .filter(p -> !requestedSkus.contains(p.getSku().toUpperCase()))
            .sorted(Comparator.comparingDouble(
                p -> ColorDistanceService.minDistanceToAny(p.getColor(), referenceColors)
            ))
            .limit(MAX_RECOMMENDATIONS)
            .collect(Collectors.toList());
    }
}
