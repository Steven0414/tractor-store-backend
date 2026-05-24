package com.tractorstore.service;

import com.tractorstore.model.AddCartItemRequest;
import com.tractorstore.model.Cart;
import com.tractorstore.model.CartItem;
import com.tractorstore.model.MiniCart;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart_items";

    private final CartSessionRegistry registry;

    public CartService(CartSessionRegistry registry) {
        this.registry = registry;
    }

    public Cart getCart(HttpSession session) {
        return new Cart(getItemList(session));
    }

    public MiniCart getMiniCart(HttpSession session) {
        Cart cart = getCart(session);
        return new MiniCart(cart.getItemCount(), cart.getTotal());
    }

    public Cart addItem(HttpSession session, AddCartItemRequest request) {
        Map<String, CartItem> cartMap = getCartMap(session);

        if (cartMap.containsKey(request.getSku())) {
            CartItem existing = cartMap.get(request.getSku());
            existing.setQuantity(existing.getQuantity() + Math.max(1, request.getQuantity()));
        } else {
            CartItem item = new CartItem(
                    request.getSku(),
                    request.getName(),
                    Math.max(1, request.getQuantity()),
                    request.getPrice()
            );
            cartMap.put(request.getSku(), item);
        }

        session.setAttribute(CART_SESSION_KEY, cartMap);
        registry.put(session.getId(), cartMap);
        return new Cart(new ArrayList<>(cartMap.values()));
    }

    @SuppressWarnings("unchecked")
    private Map<String, CartItem> getCartMap(HttpSession session) {
        Object raw = session.getAttribute(CART_SESSION_KEY);
        if (raw instanceof Map<?, ?> map) {
            return (Map<String, CartItem>) map;
        }
        return new LinkedHashMap<>();
    }

    private List<CartItem> getItemList(HttpSession session) {
        return new ArrayList<>(getCartMap(session).values());
    }
}
