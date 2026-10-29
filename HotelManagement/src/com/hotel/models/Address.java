package com.hotel.models;

public class Address {
    private int addressId;
    private int customerId;
    private String city;
    private String addressLine;
    private String postalCode;

    public Address(int addressId, int customerId, String city, String addressLine, String postalCode) {
        this.addressId = addressId;
        this.customerId = customerId;
        this.city = city;
        this.addressLine = addressLine;
        this.postalCode = postalCode;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return String.format("%s, %s (PIN: %s)", addressLine, city, postalCode);
    }
}
