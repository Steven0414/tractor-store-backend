package com.tractorstore.order.model;

public class OrderItemPayload {

    private String sku;
    private String name;
    private int quantity;
    private double price;

    public OrderItemPayload() {}

    public OrderItemPayload(String sku, String name, int quantity, double price) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}
