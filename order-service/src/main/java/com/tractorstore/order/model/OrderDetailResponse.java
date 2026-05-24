package com.tractorstore.order.model;

import java.time.Instant;
import java.util.List;

public class OrderDetailResponse {

    private String orderId;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private double total;
    private Instant createdAt;
    private List<OrderItemDetail> items;

    public OrderDetailResponse() {}

    public OrderDetailResponse(String orderId, String firstName, String lastName, String email,
                               String status, double total, Instant createdAt, List<OrderItemDetail> items) {
        this.orderId = orderId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItemDetail> getItems() { return items; }
}
