package com.angsam.aimonitoredapi.dto;

public class OrderResponse {
    private String orderId;
    private String description;
    private double amount;

    public OrderResponse() {}

    public OrderResponse(String orderId, String description, double amount) {
        this.orderId = orderId;
        this.description = description;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
