package com.tractorstore.order.api;

import com.tractorstore.order.application.OrderCommand;
import com.tractorstore.order.application.OrderService;
import com.tractorstore.order.application.OrderView;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private static final String SESSION_COOKIE = "TRACTOR_CART_SESSION";

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public OrderView placeOrder(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
                              HttpServletResponse response,
                              @RequestBody CreateOrderRequest request) {
    String resolvedSessionId = ensureSessionCookie(sessionId, response);
    return orderService.placeOrder(
        new OrderCommand(resolvedSessionId, request.pickupStoreCode(), request.buyerName(), request.buyerEmail())
    );
  }

  @GetMapping("/{id}")
  public OrderView getOrder(@PathVariable Long id) {
    return orderService.getOrder(id);
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

  public record CreateOrderRequest(
      @NotBlank String pickupStoreCode,
      @NotBlank String buyerName,
      @NotBlank String buyerEmail
  ) {
  }
}
