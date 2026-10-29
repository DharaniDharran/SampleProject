package com.hotel.models;

public class User {
    private int userId;
    private String name;
    private String phone;
    private String email;
    private UserRole role;

    public User(int userId, String name, String phone, String email, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("User[ID=%d, Name='%s', Phone='%s', Email='%s', Role=%s]",
                userId, name, phone, email, role);
    }
}
