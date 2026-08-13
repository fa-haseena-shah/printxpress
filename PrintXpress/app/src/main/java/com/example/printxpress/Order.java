package com.example.printxpress;

public class Order {
    private int orderId, customerId;
    private String orderDate, status;
    private double orderTotal;

    public Order(int orderId, int customerId, String orderDate, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Order(int orderId, int customerId, String orderDate, String status, double orderTotal) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.orderTotal = orderTotal;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public double getOrderTotal() {
        return orderTotal;
    }
}
