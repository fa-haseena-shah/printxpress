package com.example.printxpress;

import android.content.SharedPreferences;

import java.util.List;

// because there are multiple tables involved in one order, we create this class which will store
// the complete instance (order, item, delivery) of each order to easily display order history
public class HistoryHelper {
    private final int orderId;
    private final String orderDate, status, productName, customText, designTitle;
    private final String deliveryType, addressDisplay, deliveryDateTime;
    private final int qty;
    private final double orderTotal, delFee;

    public HistoryHelper(int orderId, String orderDate, String status, String productName, int qty, String customText,
                         String designTitle, String deliveryType, String addressDisplay, String deliveryDateTime,
                        double orderTotal, double delFee) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.productName = productName;
        this.qty = qty;
        this.customText = customText;
        this.designTitle = designTitle;
        this.deliveryType = deliveryType;
        this.addressDisplay = addressDisplay;
        this.deliveryDateTime = deliveryDateTime;
        this.orderTotal = orderTotal;
        this.delFee = delFee;
    }

    // only getters bc nothing needs to be set
    public int getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public String getProductName() {
        return productName;
    }

    public String getCustomText() {
        return customText;
    }

    public String getDesignTitle() {
        return designTitle;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public String getAddressDisplay() {
        return addressDisplay;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public int getQty() {
        return qty;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public double getDelFee() {
        return delFee;
    }

    //  the custom text or uploaded design for display
    public String getCustomization() {
        StringBuilder customization = new StringBuilder();
        if (customText != null && !customText.isEmpty()) {
            customization.append("Text: ").append(customText);
        }
        if (designTitle != null && !designTitle.isEmpty()) {
            if (customization.length() > 0) {
                customization.append(" • ");
            }
            customization.append("Design: ").append(designTitle);
        }
        if (customization.length() == 0) {
            return "No artwork specified";
        }
        return customization.toString();
    }

    public double getTotalAmount() {
        return orderTotal + (delFee > 0 ? delFee : 0);
    }
}

