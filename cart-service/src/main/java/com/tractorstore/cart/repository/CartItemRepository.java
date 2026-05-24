package com.tractorstore.cart.repository;

import com.tractorstore.cart.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {

    List<CartItemEntity> findBySessionId(String sessionId);

    Optional<CartItemEntity> findBySessionIdAndSku(String sessionId, String sku);

    void deleteBySessionId(String sessionId);

    void deleteBySessionIdAndSku(String sessionId, String sku);
}
