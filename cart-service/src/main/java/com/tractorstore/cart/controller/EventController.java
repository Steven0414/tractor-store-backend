package com.tractorstore.cart.controller;

import com.tractorstore.cart.model.OrderPlacedPayload;
import com.tractorstore.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/events")
public class EventController {

    private final CartService cartService;

    public EventController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * POST /internal/events/order-placed
     * Clears the cart for the session identified in the event payload.
     */
    @PostMapping("/order-placed")
    public ResponseEntity<Void> onOrderPlaced(@RequestBody OrderPlacedPayload payload) {
        cartService.clearCart(payload.getSessionId());
        return ResponseEntity.ok().build();
    }
}
