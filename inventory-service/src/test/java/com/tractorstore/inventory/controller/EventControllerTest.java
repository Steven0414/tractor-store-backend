package com.tractorstore.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.inventory.model.OrderItemPayload;
import com.tractorstore.inventory.model.OrderPlacedPayload;
import com.tractorstore.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void handleOrderPlaced_returns200AndCallsService() throws Exception {
        OrderItemPayload item = new OrderItemPayload("TRK-001", "Tractor Rojo", 2, 12500.0);
        OrderPlacedPayload payload = new OrderPlacedPayload("order-uuid", "session-abc", List.of(item));

        mockMvc.perform(post("/internal/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(inventoryService).deductStock(anyList());
    }

    @Test
    void handleOrderPlaced_emptyItems_returns200() throws Exception {
        OrderPlacedPayload payload = new OrderPlacedPayload("order-uuid", "session-abc", List.of());

        mockMvc.perform(post("/internal/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(inventoryService).deductStock(anyList());
    }
}
