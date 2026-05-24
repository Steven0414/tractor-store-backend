package com.tractorstore.inventory.controller;

import com.tractorstore.inventory.model.StockResponse;
import com.tractorstore.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void getStock_existingSku_returns200WithBody() throws Exception {
        when(inventoryService.getStock("TRK-001")).thenReturn(10);

        mockMvc.perform(get("/api/inventory/TRK-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sku").value("TRK-001"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void getStock_unknownSku_returns404() throws Exception {
        when(inventoryService.getStock("UNKNOWN"))
                .thenThrow(new NoSuchElementException("SKU not found: UNKNOWN"));

        mockMvc.perform(get("/api/inventory/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }
}
