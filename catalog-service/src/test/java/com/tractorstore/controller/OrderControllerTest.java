package com.tractorstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.model.order.OrderItem;
import com.tractorstore.model.order.PlaceOrderRequest;
import com.tractorstore.model.order.PlaceOrderResponse;
import com.tractorstore.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private OrderService orderService;

    private PlaceOrderRequest validRequest() {
        OrderItem item = new OrderItem();
        item.setSku("TRK-001"); item.setName("Tractor"); item.setQuantity(1); item.setPrice(9999.0);

        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setFirstName("John"); req.setLastName("Doe"); req.setEmail("john@test.com");
        req.setPhone("555-1234"); req.setAddress("Main St 1"); req.setCity("Lima");
        req.setPostalCode("15001"); req.setPaymentMethod("CARD");
        req.setItems(List.of(item));
        return req;
    }

    @Test
    void placeOrder_validRequest_returns201() throws Exception {
        var response = new PlaceOrderResponse("order-uuid", "CONFIRMED", 9999.0);
        when(orderService.placeOrder(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.total").value(9999.0))
                .andExpect(jsonPath("$.orderId").value("order-uuid"));
    }

    @Test
    void placeOrder_missingFields_returns422() throws Exception {
        var invalid = new PlaceOrderRequest();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalid)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void placeOrder_invalidEmail_returns422() throws Exception {
        var req = validRequest();
        req.setEmail("not-an-email");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }
}
