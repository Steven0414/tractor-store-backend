package com.tractorstore.model;

import java.util.List;

public class Cart {

    private List<CartItem> items;
    private int itemCount;
    private double total;

    public Cart() {}

    public Cart(List<CartItem> items) {
        this.items = items;
        this.itemCount = items.stream().mapToInt(CartItem::getQuantity).sum();
        this.total = items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
