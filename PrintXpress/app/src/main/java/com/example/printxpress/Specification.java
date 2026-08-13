package com.example.printxpress;

public class Specification {
    private int specId;
    private String specName, specValue;

    public Specification(int specId, String specName, String specValue) {
        this.specId = specId;
        this.specName = specName;
        this.specValue = specValue;
    }

    public int getSpecId() {
        return specId;
    }

    public String getSpecName() {
        return specName;
    }

    public String getSpecValue() {
        return specValue;
    }
}
