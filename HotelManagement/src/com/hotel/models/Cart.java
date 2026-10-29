package com.hotel.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private int cartId;
    private int customerId;
    private Integer restaurantId; // null when empty
    private final List<CartItem> items;
    private LocalDateTime updatedAt;

    public Cart(int cartId, int customerId) {
        this.cartId = cartId;
        this.customerId = customerId;
        this.restaurantId = null;
        this.items = new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CartItem getItemByMenuItemId(int itemId) {
        for (CartItem ci : items) {
            if (ci.getItemId() == itemId) {
                return ci;
            }
        }
        return null;
    }

    public void removeItem(int itemId) {
        items.removeIf(ci -> ci.getItemId() == itemId);
        if (items.isEmpty()) {
            restaurantId = null;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void clear() {
        items.clear();
        restaurantId = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
