package com.tractorstore.notifications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.notifications.model.OrderItemPayload;
import com.tractorstore.notifications.model.OrderPlacedPayload;
import com.tractorstore.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    private NotificationService notificationService;

    @Test
    void onOrderPlaced_withValidPayload_returns200AndCallsService() throws Exception {
        OrderPlacedPayload payload = new OrderPlacedPayload(
            "ORD-001",
            "session-abc",
            List.of(new OrderItemPayload("SKU-1", "Tractor Wheel", 2, 49.99))
        );

        mockMvc.perform(post("/internal/events/order-placed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk());

        verify(notificationService).handleOrderPlaced(any(OrderPlacedPayload.class));
    }

    @Test
    void onOrderPlaced_withEmptyItems_returns200() throws Exception {
        OrderPlacedPayload payload = new OrderPlacedPayload("ORD-002", "session-xyz", List.of());

        mockMvc.perform(post("/internal/events/order-placed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk());
    }
}
