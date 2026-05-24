package com.tractorstore.model.catalog;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "catalog_stores",
    indexes = @Index(name = "idx_catalog_stores_city", columnList = "city")
)
public class CatalogStoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    private String phone;

    private String email;

    private double latitude;

    private double longitude;

    @Column(name = "opening_hours")
    private String openingHours;

    public CatalogStoreEntity() {}

    public CatalogStoreEntity(String name, String address, String city, String phone,
                               String email, double latitude, double longitude, String openingHours) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingHours = openingHours;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getOpeningHours() { return openingHours; }
}
