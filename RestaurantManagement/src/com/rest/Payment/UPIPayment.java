package com.rest.Payment;

public class UPIPayment implements PaymentMethod {

    @Override
    public boolean pay(double amount) {
        System.out.printf("Paid $%.2f via UPI.%n", amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "UPI";
    }
}
