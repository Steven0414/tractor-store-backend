package com.tractorstore.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.cart.model.OrderItemPayload;
import com.tractorstore.cart.model.OrderPlacedPayload;
import com.tractorstore.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private CartService cartService;

    @Test
    void onOrderPlaced_clearsCartAndReturns200() throws Exception {
        OrderItemPayload item = new OrderItemPayload();
        item.setSku("TRK-001"); item.setName("Tractor Rojo"); item.setQuantity(2); item.setPrice(12500.0);

        OrderPlacedPayload payload = new OrderPlacedPayload();
        payload.setOrderId("order-uuid-123");
        payload.setSessionId("session-abc");
        payload.setItems(List.of(item));

        doNothing().when(cartService).clearCart(any());

        mockMvc.perform(post("/internal/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(cartService).clearCart("session-abc");
    }

    @Test
    void onOrderPlaced_withEmptyItems_clearsCartAndReturns200() throws Exception {
        OrderPlacedPayload payload = new OrderPlacedPayload();
        payload.setOrderId("order-uuid-456");
        payload.setSessionId("session-xyz");
        payload.setItems(List.of());

        doNothing().when(cartService).clearCart(any());

        mockMvc.perform(post("/internal/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(cartService).clearCart("session-xyz");
    }
}
