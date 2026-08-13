package com.example.printxpress;

import java.util.List;

public class OrderItem {
    private int orderId, itemId, designId, productId, qty;
    private String customTxt;
    private double unitPrice;

    public OrderItem(int productId, int designId, String customTxt,int qty, double unitPrice) {
        this.productId = productId;
        this.designId = designId;
        this.customTxt = customTxt;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getDesignId() {
        return designId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQty() {
        return qty;
    }

    public String getCustomTxt() {
        return customTxt;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
