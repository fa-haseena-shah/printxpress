package com.example.printxpress;

public class Design {
    private int designId, customerId;
    private String title, filePath;

    public Design(int designId, String title, String filePath, int customerId) {
        this.designId = designId;
        this.title = title;
        this.filePath = filePath;
        this.customerId = customerId;
    }

    public Design(int designId, String title, String filePath) {
        this.designId = designId;
        this.title = title;
        this.filePath = filePath;
    }

    public int getDesignId() {
        return designId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }
}
