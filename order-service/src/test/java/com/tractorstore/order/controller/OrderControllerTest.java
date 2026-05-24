package com.tractorstore.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.order.exception.GlobalExceptionHandler;
import com.tractorstore.order.model.*;
import com.tractorstore.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private PlaceOrderRequest validRequest() {
        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john@example.com");
        req.setPhone("555-0100");
        req.setAddress("123 Main St");
        req.setCity("Springfield");
        req.setPostalCode("12345");
        req.setPaymentMethod("CARD");
        req.setItems(List.of(new OrderItem("TRK-001", "Tractor Rojo", 2, 12500.0)));
        return req;
    }

    @Test
    void placeOrder_validRequest_returns201() throws Exception {
        PlaceOrderResponse resp = new PlaceOrderResponse(UUID.randomUUID().toString(), "PENDING", 25000.0);
        when(orderService.placeOrder(any(), anyString())).thenReturn(resp);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.total").value(25000.0));
    }

    @Test
    void placeOrder_missingFields_returns422() throws Exception {
        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setFirstName("John");
        // missing most required fields

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void placeOrder_invalidEmail_returns422() throws Exception {
        PlaceOrderRequest req = validRequest();
        req.setEmail("not-an-email");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getOrder_found_returns200() throws Exception {
        String id = UUID.randomUUID().toString();
        OrderDetailResponse detail = new OrderDetailResponse(
                id, "John", "Doe", "john@example.com", "PENDING", 25000.0, Instant.now(),
                List.of(new OrderItemDetail("TRK-001", "Tractor Rojo", 2, 12500.0))
        );
        when(orderService.getOrder(id)).thenReturn(detail);

        mockMvc.perform(get("/api/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(id))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].sku").value("TRK-001"));
    }

    @Test
    void getOrder_notFound_returns404() throws Exception {
        String id = UUID.randomUUID().toString();
        when(orderService.getOrder(id)).thenThrow(new NoSuchElementException("Order not found: " + id));

        mockMvc.perform(get("/api/orders/" + id))
                .andExpect(status().isNotFound());
    }
}
