package com.hotel.models;

public class Admin extends User {
    public Admin(int userId, String name, String phone, String email) {
        super(userId, name, phone, email, UserRole.ADMIN);
    }

    @Override
    public String toString() {
        return String.format("Admin[ID=%d, Name='%s', Phone='%s']",
                getUserId(), getName(), getPhone());
    }
}
