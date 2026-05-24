package com.tractorstore.catalog.infrastructure;

import com.tractorstore.catalog.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
