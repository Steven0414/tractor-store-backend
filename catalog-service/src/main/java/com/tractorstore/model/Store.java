package com.tractorstore.model;

public record Store(
    String id,
    String name,
    String address,
    String city,
    String phone,
    String email,
    double latitude,
    double longitude,
    String openingHours
) {}
