package com.tractorstore.cart.controller;

import com.tractorstore.cart.model.AddCartItemRequest;
import com.tractorstore.cart.model.Cart;
import com.tractorstore.cart.model.MiniCart;
import com.tractorstore.cart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * GET /api/cart
     * Returns the full cart (all items, total, itemCount) for the current session.
     */
    @GetMapping
    public ResponseEntity<Cart> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getCart(session));
    }

    /**
     * GET /api/cart/mini
     * Returns a lightweight summary (itemCount, total) for the current session.
     */
    @GetMapping("/mini")
    public ResponseEntity<MiniCart> getMiniCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getMiniCart(session));
    }

    /**
     * POST /api/cart/items
     * Adds or increments a product in the session cart.
     */
    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(
            HttpSession session,
            @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(session, request));
    }

    /**
     * DELETE /api/cart/items/{sku}
     * Removes a product from the session cart by SKU.
     */
    @DeleteMapping("/items/{sku}")
    public ResponseEntity<Void> removeItem(HttpSession session, @PathVariable String sku) {
        cartService.removeItem(session, sku);
        return ResponseEntity.noContent().build();
    }
}
