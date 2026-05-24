package com.tractorstore.service;

import com.tractorstore.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry that mirrors the HTTP session cart so that
 * event listeners can clear it without needing an HttpSession reference.
 */
@Component
public class CartSessionRegistry {

    private final Map<String, Map<String, CartItem>> sessions = new ConcurrentHashMap<>();

    public void put(String sessionId, Map<String, CartItem> cartMap) {
        sessions.put(sessionId, cartMap);
    }

    public void clear(String sessionId) {
        Map<String, CartItem> cart = sessions.get(sessionId);
        if (cart != null) {
            cart.clear();
        }
        sessions.remove(sessionId);
    }

    public boolean contains(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
