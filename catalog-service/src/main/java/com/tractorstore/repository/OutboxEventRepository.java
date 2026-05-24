package com.tractorstore.repository;

import com.tractorstore.model.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {}
