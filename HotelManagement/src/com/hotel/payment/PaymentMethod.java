package com.hotel.payment;

public interface PaymentMethod {
    boolean processPayment(double amount, String details);
    String getMethodName();
}
