package com.tractorstore.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_orders", indexes = @Index(name = "idx_order_orders_created_at", columnList = "created_at"))
public class OrderEntity {

    @Id
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;
    private String phone;
    private String address;
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "payment_method")
    private String paymentMethod;

    private BigDecimal total;
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    public OrderEntity() {}

    public OrderEntity(UUID id, String firstName, String lastName, String email, String phone,
                       String address, String city, String postalCode, String paymentMethod,
                       BigDecimal total, String status, Instant createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.paymentMethod = paymentMethod;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getPaymentMethod() { return paymentMethod; }
    public BigDecimal getTotal() { return total; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
