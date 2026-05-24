package com.tractorstore.cart.api;

import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.application.CartSummaryView;
import com.tractorstore.cart.application.CartView;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.web.bind.annotation.CookieValue;
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

  private static final String SESSION_COOKIE = "TRACTOR_CART_SESSION";

  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  public CartView cart(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
                       HttpServletResponse response) {
    return cartService.getCart(ensureSessionCookie(sessionId, response));
  }

  @GetMapping("/mini")
  public CartSummaryView mini(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
                              HttpServletResponse response) {
    return cartService.getMiniCart(ensureSessionCookie(sessionId, response));
  }

  @PostMapping("/items")
  public CartView addItem(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
                          HttpServletResponse response,
                          @RequestBody AddItemRequest request) {
    String resolvedSessionId = ensureSessionCookie(sessionId, response);
    return cartService.addItem(resolvedSessionId, request.sku(), request.quantity(), request.unitPrice());
  }

  @DeleteMapping("/items/{sku}")
  public CartView removeItem(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
                             HttpServletResponse response,
                             @PathVariable String sku) {
    return cartService.removeItem(ensureSessionCookie(sessionId, response), sku);
  }

  @PostMapping("/checkout")
  public void checkout(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
                       HttpServletResponse response,
                       @RequestBody CheckoutRequest request) {
    cartService.requestCheckout(
        ensureSessionCookie(sessionId, response),
        request.pickupStoreCode(),
        request.buyerName(),
        request.buyerEmail()
    );
  }

  private String ensureSessionCookie(String currentSessionId, HttpServletResponse response) {
    if (currentSessionId != null && !currentSessionId.isBlank()) {
      return currentSessionId;
    }

    String generated = UUID.randomUUID().toString();
    Cookie cookie = new Cookie(SESSION_COOKIE, generated);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(7 * 24 * 60 * 60);
    response.addCookie(cookie);
    return generated;
  }

  public record AddItemRequest(
      @NotBlank String sku,
      @Min(1) int quantity,
      BigDecimal unitPrice
  ) {
  }

  public record CheckoutRequest(
      @NotBlank String pickupStoreCode,
      @NotBlank String buyerName,
      @NotBlank String buyerEmail
  ) {
  }
}
