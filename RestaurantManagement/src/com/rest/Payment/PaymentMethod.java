package com.rest.Payment;

public interface PaymentMethod {
    boolean pay(double amount);
    String getMethodName();
}
