package com.tractorstore.inventory.service;

import com.tractorstore.inventory.model.InventoryStockEntity;
import com.tractorstore.inventory.model.OrderItemPayload;
import com.tractorstore.inventory.repository.InventoryStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryStockRepository inventoryStockRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(inventoryStockRepository);
    }

    @Test
    void getStock_existingSku_returnsQuantity() {
        when(inventoryStockRepository.findBySku("TRK-001"))
                .thenReturn(Optional.of(new InventoryStockEntity("TRK-001", 10)));

        assertEquals(10, inventoryService.getStock("TRK-001"));
    }

    @Test
    void getStock_unknownSku_throwsNoSuchElementException() {
        when(inventoryStockRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> inventoryService.getStock("UNKNOWN"));
    }

    @Test
    void deductStock_reducesQuantity() {
        InventoryStockEntity entity = new InventoryStockEntity("TRK-001", 10);
        when(inventoryStockRepository.findBySku("TRK-001")).thenReturn(Optional.of(entity));

        OrderItemPayload item = new OrderItemPayload("TRK-001", "Tractor Rojo", 3, 12500.0);
        inventoryService.deductStock(List.of(item));

        assertEquals(7, entity.getQuantity());
        verify(inventoryStockRepository).save(entity);
    }

    @Test
    void deductStock_doesNotGoBelowZero() {
        InventoryStockEntity entity = new InventoryStockEntity("TRK-001", 2);
        when(inventoryStockRepository.findBySku("TRK-001")).thenReturn(Optional.of(entity));

        OrderItemPayload item = new OrderItemPayload("TRK-001", "Tractor Rojo", 10, 12500.0);
        inventoryService.deductStock(List.of(item));

        assertEquals(0, entity.getQuantity());
        verify(inventoryStockRepository).save(entity);
    }

    @Test
    void deductStock_unknownSku_isNoOp() {
        when(inventoryStockRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

        OrderItemPayload item = new OrderItemPayload("UNKNOWN", "Unknown", 5, 100.0);
        inventoryService.deductStock(List.of(item));

        verify(inventoryStockRepository, never()).save(any());
    }

    @Test
    void deductStock_emptyList_doesNothing() {
        inventoryService.deductStock(List.of());

        verifyNoInteractions(inventoryStockRepository);
    }
}
