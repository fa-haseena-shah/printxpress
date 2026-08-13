package com.example.printxpress;

public class Product {
    private int productId;
    private String name, description, sample, categoryName;
    private double basePrice;

    public Product(int productId, String name, String description, double basePrice, String sample) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.sample = sample;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getSample() {
        return sample;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
