package com.rest.models;

public class orderItem {
    private int id;
    private int order_id;
    private int menuitem_id;
    private int quantity;
    private double unitwise;

    public orderItem(int id, int order_id, int menuitem_id, int quantity, double unitwise) {
        this.id = id;
        this.order_id = order_id;
        this.menuitem_id = menuitem_id;
        this.quantity = quantity;
        this.unitwise = unitwise;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getOrderId() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setOrderId(int orderId) {
        this.order_id = orderId;
    }

    public int getMenuitem_id() {
        return menuitem_id;
    }

    public int getMenuItemId() {
        return menuitem_id;
    }

    public void setMenuitem_id(int menuitem_id) {
        this.menuitem_id = menuitem_id;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuitem_id = menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitwise() {
        return unitwise;
    }

    public void setUnitwise(double unitwise) {
        this.unitwise = unitwise;
    }

    public double getTotalPrice() {
        return quantity * unitwise;
    }

    @Override
    public String toString() {
        return "Item ID: " + id + ", Order ID: " + order_id + ", MenuItem ID: " + menuitem_id + ", Quantity: " + quantity + ", Unitwise: " + unitwise + ", Total: " + getTotalPrice();
    }
}
