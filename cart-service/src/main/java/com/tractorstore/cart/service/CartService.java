package com.tractorstore.cart.service;

import com.tractorstore.cart.model.AddCartItemRequest;
import com.tractorstore.cart.model.Cart;
import com.tractorstore.cart.model.CartItem;
import com.tractorstore.cart.model.CartItemEntity;
import com.tractorstore.cart.model.MiniCart;
import com.tractorstore.cart.repository.CartItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional(readOnly = true)
    public Cart getCart(HttpSession session) {
        List<CartItem> items = cartItemRepository
            .findBySessionId(session.getId())
            .stream()
            .map(CartService::toDto)
            .toList();
        return new Cart(items);
    }

    @Transactional(readOnly = true)
    public MiniCart getMiniCart(HttpSession session) {
        Cart cart = getCart(session);
        return new MiniCart(cart.getItemCount(), cart.getTotal());
    }

    public Cart addItem(HttpSession session, AddCartItemRequest request) {
        String sessionId = session.getId();
        cartItemRepository.findBySessionIdAndSku(sessionId, request.getSku())
            .ifPresentOrElse(
                existing -> {
                    existing.setQuantity(existing.getQuantity() + Math.max(1, request.getQuantity()));
                    cartItemRepository.save(existing);
                },
                () -> {
                    CartItemEntity entity = new CartItemEntity(
                        sessionId,
                        request.getSku(),
                        request.getName(),
                        Math.max(1, request.getQuantity()),
                        BigDecimal.valueOf(request.getPrice())
                    );
                    cartItemRepository.save(entity);
                }
            );
        return getCart(session);
    }

    public void removeItem(HttpSession session, String sku) {
        cartItemRepository.deleteBySessionIdAndSku(session.getId(), sku);
    }

    public void clearCart(String sessionId) {
        cartItemRepository.deleteBySessionId(sessionId);
    }

    private static CartItem toDto(CartItemEntity entity) {
        return new CartItem(
            entity.getSku(),
            entity.getName(),
            entity.getQuantity(),
            entity.getPrice().doubleValue()
        );
    }
}
