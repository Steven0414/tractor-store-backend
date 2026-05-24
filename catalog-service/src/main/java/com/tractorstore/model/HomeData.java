package com.tractorstore.model;

import java.util.List;

public class HomeData {
    private String bannerTitle;
    private String bannerSubtitle;
    private List<Category> featuredCategories;
    private List<Product> featuredProducts;

    public HomeData() {}

    public HomeData(String bannerTitle, String bannerSubtitle,
                    List<Category> featuredCategories, List<Product> featuredProducts) {
        this.bannerTitle = bannerTitle;
        this.bannerSubtitle = bannerSubtitle;
        this.featuredCategories = featuredCategories;
        this.featuredProducts = featuredProducts;
    }

    public String getBannerTitle() { return bannerTitle; }
    public void setBannerTitle(String bannerTitle) { this.bannerTitle = bannerTitle; }

    public String getBannerSubtitle() { return bannerSubtitle; }
    public void setBannerSubtitle(String bannerSubtitle) { this.bannerSubtitle = bannerSubtitle; }

    public List<Category> getFeaturedCategories() { return featuredCategories; }
    public void setFeaturedCategories(List<Category> featuredCategories) { this.featuredCategories = featuredCategories; }

    public List<Product> getFeaturedProducts() { return featuredProducts; }
    public void setFeaturedProducts(List<Product> featuredProducts) { this.featuredProducts = featuredProducts; }
}
