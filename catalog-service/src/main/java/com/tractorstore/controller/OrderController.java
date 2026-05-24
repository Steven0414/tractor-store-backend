package com.tractorstore.controller;

import com.tractorstore.model.order.PlaceOrderRequest;
import com.tractorstore.model.order.PlaceOrderResponse;
import com.tractorstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST /api/orders
     * Places a new order for the authenticated session.
     */
    @PostMapping
    public ResponseEntity<PlaceOrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            HttpSession session) {
        PlaceOrderResponse response = orderService.placeOrder(request, session.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
