package com.tractorstore.service;

import com.tractorstore.model.Category;
import com.tractorstore.model.HomeData;
import com.tractorstore.model.Product;
import com.tractorstore.model.Store;
import com.tractorstore.model.catalog.CatalogCategoryEntity;
import com.tractorstore.model.catalog.CatalogProductEntity;
import com.tractorstore.model.catalog.CatalogStoreEntity;
import com.tractorstore.repository.CatalogCategoryRepository;
import com.tractorstore.repository.CatalogProductRepository;
import com.tractorstore.repository.CatalogStoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private static final int MAX_RECOMMENDATIONS = 6;
    private static final int FEATURED_COUNT = 4;

    private final CatalogProductRepository productRepository;
    private final CatalogCategoryRepository categoryRepository;
    private final CatalogStoreRepository storeRepository;

    public CatalogService(CatalogProductRepository productRepository,
                          CatalogCategoryRepository categoryRepository,
                          CatalogStoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.storeRepository = storeRepository;
    }

    public HomeData getHome() {
        List<Product> products = toProductDtos(productRepository.findAll());
        List<Category> categories = toCategoryDtos(categoryRepository.findAll());
        List<Product> featured = products.subList(0, Math.min(FEATURED_COUNT, products.size()));
        return new HomeData(
            "Bienvenido a The Tractor Store",
            "Encuentra el tractor perfecto para tu campo",
            categories,
            featured
        );
    }

    public List<Product> getProductsByFilter(String filter) {
        if ("all".equalsIgnoreCase(filter)) {
            return toProductDtos(productRepository.findAll());
        }
        return toProductDtos(productRepository.findByCategory(filter));
    }

    public List<Store> getStores() {
        return toStoreDtos(storeRepository.findAll());
    }

    /**
     * Returns up to {@value #MAX_RECOMMENDATIONS} products whose colors are closest
     * (Euclidean RGB distance) to the colors of the requested SKUs.
     */
    public List<Product> getRecommendations(String skusCsv) {
        Set<String> requestedSkus = Arrays.stream(skusCsv.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(String::toUpperCase)
            .collect(Collectors.toUnmodifiableSet());

        List<Product> allProducts = toProductDtos(productRepository.findAll());

        Map<String, Product> skuIndex = allProducts.stream()
            .collect(Collectors.toMap(p -> p.sku().toUpperCase(), p -> p));

        List<String> referenceColors = requestedSkus.stream()
            .filter(skuIndex::containsKey)
            .map(sku -> skuIndex.get(sku).color())
            .toList();

        if (referenceColors.isEmpty()) {
            return List.of();
        }

        return allProducts.stream()
            .filter(p -> !requestedSkus.contains(p.sku().toUpperCase()))
            .sorted(java.util.Comparator.comparingDouble(
                p -> ColorDistanceService.minDistanceToAny(p.color(), referenceColors)
            ))
            .limit(MAX_RECOMMENDATIONS)
            .toList();
    }

    // ── Entity → DTO mappers ───────────────────────────────────────────────

    private static Product toProductDto(CatalogProductEntity e) {
        return new Product(
            e.getId().toString(),
            e.getSku(),
            e.getName(),
            e.getPrice().doubleValue(),
            e.getImageUrl(),
            e.getCategory(),
            e.getColor(),
            e.getMotor()
        );
    }

    private static Category toCategoryDto(CatalogCategoryEntity e) {
        return new Category(
            e.getId().toString(),
            e.getName(),
            e.getFilter(),
            e.getImageUrl(),
            e.getDescription()
        );
    }

    private static Store toStoreDto(CatalogStoreEntity e) {
        return new Store(
            e.getId().toString(),
            e.getName(),
            e.getAddress(),
            e.getCity(),
            e.getPhone(),
            e.getEmail(),
            e.getLatitude(),
            e.getLongitude(),
            e.getOpeningHours()
        );
    }

    private static List<Product> toProductDtos(List<CatalogProductEntity> entities) {
        return entities.stream().map(CatalogService::toProductDto).toList();
    }

    private static List<Category> toCategoryDtos(List<CatalogCategoryEntity> entities) {
        return entities.stream().map(CatalogService::toCategoryDto).toList();
    }

    private static List<Store> toStoreDtos(List<CatalogStoreEntity> entities) {
        return entities.stream().map(CatalogService::toStoreDto).toList();
    }
}

