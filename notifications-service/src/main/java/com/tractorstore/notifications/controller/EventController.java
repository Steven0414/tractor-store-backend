package com.tractorstore.notifications.controller;

import com.tractorstore.notifications.model.OrderPlacedPayload;
import com.tractorstore.notifications.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/events")
public class EventController {

    private final NotificationService notificationService;

    public EventController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/order-placed")
    public ResponseEntity<Void> onOrderPlaced(@RequestBody OrderPlacedPayload payload) {
        notificationService.handleOrderPlaced(payload);
        return ResponseEntity.ok().build();
    }
}
