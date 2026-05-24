package com.tractorstore.order.controller;

import com.tractorstore.order.model.OrderDetailResponse;
import com.tractorstore.order.model.PlaceOrderRequest;
import com.tractorstore.order.model.PlaceOrderResponse;
import com.tractorstore.order.service.OrderService;
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

    @PostMapping
    public ResponseEntity<PlaceOrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request,
                                                         HttpSession session) {
        PlaceOrderResponse response = orderService.placeOrder(request, session.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable String id) {
        OrderDetailResponse response = orderService.getOrder(id);
        return ResponseEntity.ok(response);
    }
}
