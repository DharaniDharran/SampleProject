package com.rest.models;

public class menuItem {
    private int id;
    private String name;
    private double price;
    private String category;
    private boolean is_veg;

    public menuItem(int id, String name, double price, String category, boolean is_veg) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.is_veg = is_veg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isVeg() {
        return is_veg;
    }

    public boolean getIs_veg() {
        return is_veg;
    }

    public boolean isIs_veg() {
        return is_veg;
    }

    public void setIs_veg(boolean is_veg) {
        this.is_veg = is_veg;
    }

    public void setVeg(boolean is_veg) {
        this.is_veg = is_veg;
    }

    @Override
    public String toString() {
        return "MenuItem ID: " + id + ", Name: " + name + ", Price: " + price + ", Category: " + category + ", Veg: " + (is_veg ? "Yes" : "No");
    }
}
