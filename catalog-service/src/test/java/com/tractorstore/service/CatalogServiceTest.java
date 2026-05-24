package com.tractorstore.service;

import com.tractorstore.model.HomeData;
import com.tractorstore.model.Product;
import com.tractorstore.model.Store;
import com.tractorstore.model.catalog.CatalogCategoryEntity;
import com.tractorstore.model.catalog.CatalogProductEntity;
import com.tractorstore.model.catalog.CatalogStoreEntity;
import com.tractorstore.repository.CatalogCategoryRepository;
import com.tractorstore.repository.CatalogProductRepository;
import com.tractorstore.repository.CatalogStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock private CatalogProductRepository productRepository;
    @Mock private CatalogCategoryRepository categoryRepository;
    @Mock private CatalogStoreRepository storeRepository;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogService(productRepository, categoryRepository, storeRepository);
    }

    private CatalogProductEntity makeProduct(String sku, String category, String color) throws Exception {
        CatalogProductEntity e = new CatalogProductEntity(
                sku, "Product " + sku, BigDecimal.valueOf(9999.99), "/img.jpg", category, color, "Diesel");
        Field f = CatalogProductEntity.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(e, UUID.randomUUID());
        return e;
    }

    private CatalogCategoryEntity makeCategory(String name, String filter) throws Exception {
        CatalogCategoryEntity e = new CatalogCategoryEntity(name, filter, "/cat.jpg", "Desc");
        Field f = CatalogCategoryEntity.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(e, UUID.randomUUID());
        return e;
    }

    private CatalogStoreEntity makeStore(String name, String city) throws Exception {
        CatalogStoreEntity e = new CatalogStoreEntity(name, "Street 1", city, "555-1234",
                "store@test.com", 4.60, -74.08, "Mon-Sat 9-5");
        Field f = CatalogStoreEntity.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(e, UUID.randomUUID());
        return e;
    }

    @Test
    void getHome_returnsFeaturedProductsAndCategories() throws Exception {
        var products = List.of(
                makeProduct("P1", "tractor", "#FF0000"),
                makeProduct("P2", "tractor", "#00FF00"),
                makeProduct("P3", "auto", "#0000FF"),
                makeProduct("P4", "auto", "#FFFF00"),
                makeProduct("P5", "auto", "#FF00FF")
        );
        var categories = List.of(makeCategory("Tractores", "tractor"));
        when(productRepository.findAll()).thenReturn(products);
        when(categoryRepository.findAll()).thenReturn(categories);

        HomeData home = catalogService.getHome();

        assertNotNull(home);
        assertEquals(4, home.featuredProducts().size());
        assertEquals(1, home.featuredCategories().size());
        assertNotNull(home.bannerTitle());
    }

    @Test
    void getHome_withFewerThanFourProducts_returnsAll() throws Exception {
        when(productRepository.findAll()).thenReturn(List.of(makeProduct("P1", "tractor", "#FF0000")));
        when(categoryRepository.findAll()).thenReturn(List.of());

        HomeData home = catalogService.getHome();

        assertEquals(1, home.featuredProducts().size());
    }

    @Test
    void getProductsByFilter_all_returnsAllProducts() throws Exception {
        when(productRepository.findAll()).thenReturn(List.of(makeProduct("P1", "tractor", "#FF0000")));

        List<Product> result = catalogService.getProductsByFilter("all");

        assertEquals(1, result.size());
        verify(productRepository).findAll();
        verify(productRepository, never()).findByCategory(any());
    }

    @Test
    void getProductsByFilter_ALL_isCaseInsensitive() throws Exception {
        when(productRepository.findAll()).thenReturn(List.of());

        catalogService.getProductsByFilter("ALL");

        verify(productRepository).findAll();
    }

    @Test
    void getProductsByFilter_category_delegatesToRepository() throws Exception {
        var products = List.of(makeProduct("P1", "tractor", "#FF0000"));
        when(productRepository.findByCategory("tractor")).thenReturn(products);

        List<Product> result = catalogService.getProductsByFilter("tractor");

        assertEquals(1, result.size());
        verify(productRepository).findByCategory("tractor");
    }

    @Test
    void getStores_returnsMappedStores() throws Exception {
        var stores = List.of(makeStore("Store A", "Lima"), makeStore("Store B", "Bogotá"));
        when(storeRepository.findAll()).thenReturn(stores);

        List<Store> result = catalogService.getStores();

        assertEquals(2, result.size());
        assertEquals("Store A", result.get(0).name());
    }

    @Test
    void getRecommendations_unknownSku_returnsEmpty() throws Exception {
        when(productRepository.findAll()).thenReturn(List.of(makeProduct("P1", "tractor", "#FF0000")));

        List<Product> result = catalogService.getRecommendations("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void getRecommendations_sortedByColorDistance() throws Exception {
        var ref  = makeProduct("TRK-001", "tractor", "#FF0000");
        var near = makeProduct("AUT-002", "auto", "#FF3300");
        var far  = makeProduct("AUT-003", "auto", "#0000FF");
        when(productRepository.findAll()).thenReturn(List.of(ref, near, far));

        List<Product> result = catalogService.getRecommendations("TRK-001");

        assertEquals(2, result.size());
        assertEquals("AUT-002", result.get(0).sku());
    }

    @Test
    void getRecommendations_limitedToSix() throws Exception {
        var ref = makeProduct("REF", "tractor", "#FF0000");
        var others = new ArrayList<CatalogProductEntity>();
        for (int i = 1; i <= 10; i++) {
            others.add(makeProduct("A" + i, "auto", "#FF" + String.format("%02X", i * 10) + "00"));
        }
        var all = new ArrayList<CatalogProductEntity>();
        all.add(ref);
        all.addAll(others);
        when(productRepository.findAll()).thenReturn(all);

        List<Product> result = catalogService.getRecommendations("REF");

        assertEquals(6, result.size());
    }

    @Test
    void getRecommendations_requestedSkuExcludedFromResults() throws Exception {
        var p1 = makeProduct("TRK-001", "tractor", "#FF0000");
        var p2 = makeProduct("AUT-002", "auto", "#FF6600");
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = catalogService.getRecommendations("TRK-001");

        assertTrue(result.stream().noneMatch(p -> "TRK-001".equalsIgnoreCase(p.sku())));
    }

    @Test
    void getRecommendations_csvWithWhitespace_parsedCorrectly() throws Exception {
        var p1 = makeProduct("TRK-001", "tractor", "#FF0000");
        var p2 = makeProduct("AUT-002", "auto", "#FF6600");
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = catalogService.getRecommendations(" TRK-001 ");

        assertEquals(1, result.size());
    }

    @Test
    void getRecommendations_emptyCsvSegments_ignored() throws Exception {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = catalogService.getRecommendations(",,,");

        assertTrue(result.isEmpty());
    }
}
