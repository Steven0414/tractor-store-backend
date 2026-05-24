package com.tractorstore.model;

public class Product {
    private String id;
    private String sku;
    private String name;
    private double price;
    private String imageUrl;
    private String category;
    private String color;
    private String motor;

    public Product() {}

    public Product(String id, String sku, String name, double price,
                   String imageUrl, String category, String color, String motor) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.color = color;
        this.motor = motor;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMotor() { return motor; }
    public void setMotor(String motor) { this.motor = motor; }
}
