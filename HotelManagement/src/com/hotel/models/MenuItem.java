package com.hotel.models;

public class MenuItem {
    private int itemId;
    private int restaurantId;
    private String name;
    private double price;
    private ItemCategory category;
    private boolean isAvailable;

    public MenuItem(int itemId, int restaurantId, String name, double price, ItemCategory category, boolean isAvailable) {
        if (price <= 0) {
            throw new IllegalArgumentException("Menu item price must be greater than zero.");
        }
        this.itemId = itemId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
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
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        this.price = price;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return String.format("[#%d] %s - Rs. %.2f (%s) - %s",
                itemId, name, price, category, isAvailable ? "Available" : "UNAVAILABLE");
    }
}
