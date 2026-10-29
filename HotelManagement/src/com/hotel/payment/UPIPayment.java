package com.hotel.payment;

public class UPIPayment implements PaymentMethod {

    @Override
    public boolean processPayment(double amount, String vpa) {
        if (vpa == null || !vpa.contains("@")) {
            System.out.println("[UPI] Invalid UPI ID format. Expected 'username@bank'.");
            return false;
        }
        System.out.printf("[UPI] Payment of Rs.%.2f initiated via UPI ID (%s)... SUCCESS.%n", amount, vpa);
        return true;
    }

    @Override
    public String getMethodName() {
        return "UPI";
    }
}
