package com.hotel.models;

public class CartItem {
    private int cartItemId;
    private int cartId;
    private int itemId;
    private int quantity;

    public CartItem(int cartItemId, int cartId, int itemId, int quantity) {
        if (quantity <= 0 || quantity > 10) {
            throw new IllegalArgumentException("Quantity must be between 1 and 10.");
        }
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0 || quantity > 10) {
            throw new IllegalArgumentException("Quantity must be between 1 and 10.");
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("CartItem[ID=%d, ItemID=%d, Qty=%d]", cartItemId, itemId, quantity);
    }
}
