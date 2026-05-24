package com.tractorstore.service;

import com.tractorstore.model.AddCartItemRequest;
import com.tractorstore.model.Cart;
import com.tractorstore.model.CartItem;
import com.tractorstore.model.MiniCart;
import com.tractorstore.model.cart.CartItemEntity;
import com.tractorstore.repository.CartItemRepository;
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

    private static CartItem toDto(CartItemEntity entity) {
        return new CartItem(
            entity.getSku(),
            entity.getName(),
            entity.getQuantity(),
            entity.getPrice().doubleValue()
        );
    }
}

