package com.tractorstore.cart.model;

public class MiniCart {

    private int itemCount;
    private double total;

    public MiniCart() {}

    public MiniCart(int itemCount, double total) {
        this.itemCount = itemCount;
        this.total = total;
    }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
