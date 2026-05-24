package com.tractorstore.controller;

import com.tractorstore.model.*;
import com.tractorstore.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private CatalogService catalogService;

    @Test
    void getHome_returns200WithBody() throws Exception {
        var homeData = new HomeData("Title", "Subtitle", List.of(), List.of());
        when(catalogService.getHome()).thenReturn(homeData);

        mockMvc.perform(get("/api/catalog/home"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bannerTitle").value("Title"));
    }

    @Test
    void getCategories_returns200WithProducts() throws Exception {
        var product = new Product("1", "TRK-001", "Tractor", 9999.0, "/img.jpg", "tractor", "#FF0000", "V8");
        when(catalogService.getProductsByFilter("tractor")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/catalog/categories/tractor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("TRK-001"))
                .andExpect(jsonPath("$[0].price").value(9999.0));
    }

    @Test
    void getCategories_emptyResult_returns200EmptyArray() throws Exception {
        when(catalogService.getProductsByFilter("unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/catalog/categories/unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getStores_returns200WithStores() throws Exception {
        var store = new Store("1", "Store A", "Main St", "Lima", "555", "a@b.com", 4.0, -74.0, "9-5");
        when(catalogService.getStores()).thenReturn(List.of(store));

        mockMvc.perform(get("/api/catalog/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Store A"))
                .andExpect(jsonPath("$[0].city").value("Lima"));
    }

    @Test
    void getRecommendations_returns200WithProducts() throws Exception {
        var product = new Product("2", "AUT-002", "Auto", 5000.0, "/img2.jpg", "auto", "#00FF00", "V6");
        when(catalogService.getRecommendations("TRK-001")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/catalog/recommendations").param("skus", "TRK-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("AUT-002"));
    }
}
