package com.tractorstore.cart.service;

import com.tractorstore.cart.model.AddCartItemRequest;
import com.tractorstore.cart.model.Cart;
import com.tractorstore.cart.model.CartItemEntity;
import com.tractorstore.cart.model.MiniCart;
import com.tractorstore.cart.repository.CartItemRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.lenient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartItemRepository cartItemRepository;
    @Mock private HttpSession session;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartItemRepository);
        lenient().when(session.getId()).thenReturn("test-session-id");
    }

    private CartItemEntity entity(String sku, int qty, double price) {
        return new CartItemEntity("test-session-id", sku, "Product " + sku, qty, BigDecimal.valueOf(price));
    }

    @Test
    void getCart_emptySession_returnsEmptyCart() {
        when(cartItemRepository.findBySessionId("test-session-id")).thenReturn(List.of());

        Cart cart = cartService.getCart(session);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(0.0, cart.getTotal());
    }

    @Test
    void getCart_withItems_returnsCorrectTotals() {
        var items = List.of(entity("SKU-1", 2, 100.0), entity("SKU-2", 1, 50.0));
        when(cartItemRepository.findBySessionId("test-session-id")).thenReturn(items);

        Cart cart = cartService.getCart(session);

        assertEquals(2, cart.getItems().size());
        assertEquals(3, cart.getItemCount());
        assertEquals(250.0, cart.getTotal(), 0.001);
    }

    @Test
    void getMiniCart_returnsCorrectSummary() {
        when(cartItemRepository.findBySessionId("test-session-id"))
                .thenReturn(List.of(entity("SKU-1", 3, 200.0)));

        MiniCart miniCart = cartService.getMiniCart(session);

        assertEquals(3, miniCart.getItemCount());
        assertEquals(600.0, miniCart.getTotal(), 0.001);
    }

    @Test
    void addItem_newItem_createsEntity() {
        AddCartItemRequest req = new AddCartItemRequest();
        req.setSku("SKU-1"); req.setName("Tractor"); req.setQuantity(1); req.setPrice(500.0);

        when(cartItemRepository.findBySessionIdAndSku("test-session-id", "SKU-1"))
                .thenReturn(Optional.empty());
        when(cartItemRepository.findBySessionId("test-session-id")).thenReturn(List.of());

        cartService.addItem(session, req);

        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void addItem_existingItem_incrementsQuantity() {
        CartItemEntity existing = entity("SKU-1", 2, 500.0);
        AddCartItemRequest req = new AddCartItemRequest();
        req.setSku("SKU-1"); req.setName("Tractor"); req.setQuantity(3); req.setPrice(500.0);

        when(cartItemRepository.findBySessionIdAndSku("test-session-id", "SKU-1"))
                .thenReturn(Optional.of(existing));
        when(cartItemRepository.findBySessionId("test-session-id")).thenReturn(List.of());

        cartService.addItem(session, req);

        assertEquals(5, existing.getQuantity());
        verify(cartItemRepository).save(existing);
    }

    @Test
    void addItem_zeroQuantity_treatedAsOne() {
        AddCartItemRequest req = new AddCartItemRequest();
        req.setSku("SKU-NEW"); req.setName("New Tractor"); req.setQuantity(0); req.setPrice(300.0);

        when(cartItemRepository.findBySessionIdAndSku("test-session-id", "SKU-NEW"))
                .thenReturn(Optional.empty());
        when(cartItemRepository.findBySessionId("test-session-id")).thenReturn(List.of());

        cartService.addItem(session, req);

        ArgumentCaptor<CartItemEntity> captor = ArgumentCaptor.forClass(CartItemEntity.class);
        verify(cartItemRepository).save(captor.capture());
        assertEquals(1, captor.getValue().getQuantity());
    }

    @Test
    void addItem_negativeQuantity_treatedAsOne() {
        AddCartItemRequest req = new AddCartItemRequest();
        req.setSku("SKU-X"); req.setName("X"); req.setQuantity(-5); req.setPrice(100.0);

        when(cartItemRepository.findBySessionIdAndSku("test-session-id", "SKU-X"))
                .thenReturn(Optional.empty());
        when(cartItemRepository.findBySessionId("test-session-id")).thenReturn(List.of());

        cartService.addItem(session, req);

        ArgumentCaptor<CartItemEntity> captor = ArgumentCaptor.forClass(CartItemEntity.class);
        verify(cartItemRepository).save(captor.capture());
        assertEquals(1, captor.getValue().getQuantity());
    }

    @Test
    void removeItem_delegatesToRepository() {
        cartService.removeItem(session, "SKU-1");

        verify(cartItemRepository).deleteBySessionIdAndSku("test-session-id", "SKU-1");
    }

    @Test
    void clearCart_delegatesToRepository() {
        cartService.clearCart("some-session-id");

        verify(cartItemRepository).deleteBySessionId("some-session-id");
    }
}
