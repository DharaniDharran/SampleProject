package com.rest.models;

public class payment {

    private final int id;
    private int order_id;
    private final double amount;
    private final String method;
    private final String status;

    public payment(int id, double amount, String method, String status) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public payment(int id, int order_id, double amount, String method, String status) {
        this.id = id;
        this.order_id = order_id;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getOrderId() {
        return order_id;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Payment ID: " + id + 
               (order_id > 0 ? ", Order ID: " + order_id : "") + 
               ", Amount: $" + amount + 
               ", Method: " + method + 
               ", Status: " + status;
    }
}
