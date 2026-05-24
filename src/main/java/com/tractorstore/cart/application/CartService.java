package com.tractorstore.cart.application;

import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartItem;
import com.tractorstore.cart.infrastructure.CartRepository;
import com.tractorstore.shared.events.CheckoutRequestedEvent;
import com.tractorstore.shared.events.OrderLinePayload;
import com.tractorstore.shared.events.OrderPlacedEvent;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CartService implements CartQueryFacade {

  private final CartRepository cartRepository;
  private final ApplicationEventPublisher eventPublisher;

  public CartService(CartRepository cartRepository, ApplicationEventPublisher eventPublisher) {
    this.cartRepository = cartRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public CartView getCart(String sessionId) {
    Cart cart = getOrCreate(sessionId);
    return toView(cart);
  }

  @Transactional
  public CartSummaryView getMiniCart(String sessionId) {
    Cart cart = getOrCreate(sessionId);
    int quantity = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    return new CartSummaryView(sessionId, quantity);
  }

  @Transactional
  public CartView addItem(String sessionId, String sku, int quantity, BigDecimal unitPrice) {
    Cart cart = getOrCreate(sessionId);
    cart.addOrReplaceItem(sku, quantity, unitPrice);
    return toView(cart);
  }

  @Transactional
  public CartView removeItem(String sessionId, String sku) {
    Cart cart = getOrCreate(sessionId);
    cart.removeItem(sku);
    return toView(cart);
  }

  @Transactional
  public void requestCheckout(String sessionId, String pickupStoreCode, String buyerName, String buyerEmail) {
    getOrCreate(sessionId);
    eventPublisher.publishEvent(new CheckoutRequestedEvent(sessionId, pickupStoreCode, buyerName, buyerEmail));
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderLinePayload> getOrderLines(String sessionId) {
    Cart cart = getOrCreate(sessionId);
    return cart.getItems().stream()
        .map(item -> new OrderLinePayload(item.getSku(), item.getQuantity()))
        .toList();
  }

  @Override
  @Transactional
  public void clearCart(String sessionId) {
    cartRepository.findBySessionId(sessionId).ifPresent(Cart::clear);
  }

  @Transactional
  @TransactionalEventListener
  public void onOrderPlaced(OrderPlacedEvent event) {
    clearCart(event.sessionId());
  }

  private Cart getOrCreate(String sessionId) {
    return cartRepository.findBySessionId(sessionId)
        .orElseGet(() -> cartRepository.save(new Cart(sessionId)));
  }

  private CartView toView(Cart cart) {
    List<CartView.Item> items = cart.getItems().stream()
        .map(item -> new CartView.Item(
            item.getSku(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        ))
        .toList();

    BigDecimal total = items.stream()
        .map(CartView.Item::subtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new CartView(cart.getSessionId(), items, total);
  }
}
