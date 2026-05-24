package com.tractorstore.model;

public record Product(
    String id,
    String sku,
    String name,
    double price,
    String imageUrl,
    String category,
    String color,
    String motor
) {}
