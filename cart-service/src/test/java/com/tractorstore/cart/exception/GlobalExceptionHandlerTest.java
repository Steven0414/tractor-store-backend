package com.tractorstore.cart.exception;

import com.tractorstore.cart.controller.CartController;
import com.tractorstore.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private CartService cartService;

    @Test
    void illegalArgument_returns400() throws Exception {
        when(cartService.getCart(any())).thenThrow(new IllegalArgumentException("bad input"));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("bad input"));
    }

    @Test
    void noSuchElement_returns404() throws Exception {
        when(cartService.getCart(any())).thenThrow(new NoSuchElementException("not found"));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void genericException_returns500() throws Exception {
        when(cartService.getCart(any())).thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"));
    }

    @Test
    void validationException_returns422() throws Exception {
        // POST /api/cart/items with a body that fails @Valid (empty JSON triggers binding)
        when(cartService.addItem(any(), any())).thenThrow(new IllegalArgumentException("sku required"));

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
