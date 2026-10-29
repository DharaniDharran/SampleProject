package com.hotel.models;

import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private int orderId;
    private String paymentMethod;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    public Payment(int paymentId, int orderId, String paymentMethod, double amount, PaymentStatus status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.paymentDate = LocalDateTime.now();
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return String.format("Payment #%d | Order #%d | Method: %s | Amount: Rs.%.2f | Status: %s | Date: %s",
                paymentId, orderId, paymentMethod, amount, status, paymentDate);
    }
}
