package com.tractorstore.model;

public record Category(
    String id,
    String name,
    String filter,
    String imageUrl,
    String description
) {}
