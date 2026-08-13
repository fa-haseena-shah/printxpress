package com.example.printxpress;

public class Delivery {
    private int deliveryId, orderId, addressId;
    private String delOpt, dateTime;
    private double delFee;

    public Delivery(int deliveryId, int orderId, int addressId, String delOpt, String dateTime, double delFee) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.addressId = addressId;
        this.delOpt = delOpt;
        this.dateTime = dateTime;
        this.delFee = delFee;
    }

    public Delivery(int addressId, String delOpt, String dateTime, double delFee) {
        this.addressId = addressId;
        this.delOpt = delOpt;
        this.dateTime = dateTime;
        this.delFee = delFee;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getAddressId() {
        return addressId;
    }

    public String getDelOpt() {
        return delOpt;
    }

    public String getDateTime() {
        return dateTime;
    }

    public double getDelFee() {
        return delFee;
    }
}
