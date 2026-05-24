package com.tractorstore.model;

import java.util.List;

public record HomeData(
    String bannerTitle,
    String bannerSubtitle,
    List<Category> featuredCategories,
    List<Product> featuredProducts
) {}
