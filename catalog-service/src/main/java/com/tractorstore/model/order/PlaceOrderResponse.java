package com.tractorstore.model.order;

public class PlaceOrderResponse {

    private String orderId;
    private String status;
    private double total;

    public PlaceOrderResponse(String orderId, String status, double total) {
        this.orderId = orderId;
        this.status = status;
        this.total = total;
    }

    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
}
