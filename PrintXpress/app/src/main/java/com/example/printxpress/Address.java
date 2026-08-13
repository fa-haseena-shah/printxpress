package com.example.printxpress;

public class Address {
    private int addressId, customerId;
    private String houseNo, streetName, suburb, city;

    public Address(int addressId, int customerId, String houseNo, String streetName, String suburb, String city) {
        this.addressId = addressId;
        this.customerId = customerId;
        this.houseNo = houseNo;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
    }

    public int getAddressId() { return addressId; }
    public int getCustomerId() { return customerId; }
    public String getCity() { return city; }

    // builds a string we call whenever we need to display the address
    public String addressDisplay() {
        return houseNo + ", " + streetName + ", " + suburb + ", " + city;
    }
}