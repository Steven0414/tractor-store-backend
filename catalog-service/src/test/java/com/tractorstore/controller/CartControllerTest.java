package com.tractorstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.model.AddCartItemRequest;
import com.tractorstore.model.Cart;
import com.tractorstore.model.CartItem;
import com.tractorstore.model.MiniCart;
import com.tractorstore.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private CartService cartService;

    @Test
    void getCart_returns200WithCartData() throws Exception {
        var cart = new Cart(List.of(new CartItem("TRK-001", "Tractor", 1, 9999.0)));
        when(cartService.getCart(any())).thenReturn(cart);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(1))
                .andExpect(jsonPath("$.total").value(9999.0));
    }

    @Test
    void getCart_emptyCart_returns200() throws Exception {
        when(cartService.getCart(any())).thenReturn(new Cart(List.of()));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(0))
                .andExpect(jsonPath("$.total").value(0.0));
    }

    @Test
    void getMiniCart_returns200WithSummary() throws Exception {
        when(cartService.getMiniCart(any())).thenReturn(new MiniCart(3, 750.0));

        mockMvc.perform(get("/api/cart/mini"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(3))
                .andExpect(jsonPath("$.total").value(750.0));
    }

    @Test
    void addItem_returns200WithUpdatedCart() throws Exception {
        var req = new AddCartItemRequest();
        req.setSku("TRK-001"); req.setName("Tractor"); req.setQuantity(2); req.setPrice(9999.0);

        var cart = new Cart(List.of(new CartItem("TRK-001", "Tractor", 2, 9999.0)));
        when(cartService.addItem(any(), any())).thenReturn(cart);

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(2))
                .andExpect(jsonPath("$.total").value(19998.0));
    }
}
