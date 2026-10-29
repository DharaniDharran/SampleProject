package com.hotel.models;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private final List<Address> addresses;

    public Customer(int userId, String name, String phone, String email) {
        super(userId, name, phone, email, UserRole.CUSTOMER);
        this.addresses = new ArrayList<>();
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public Address getAddressById(int addressId) {
        for (Address addr : addresses) {
            if (addr.getAddressId() == addressId) {
                return addr;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Customer[ID=%d, Name='%s', Phone='%s', Addresses=%d]",
                getUserId(), getName(), getPhone(), addresses.size());
    }
}
