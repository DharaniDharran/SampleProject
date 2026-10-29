package com.rest.Payment;

public class CashPayment implements PaymentMethod {

    @Override
    public boolean pay(double amount) {
        System.out.printf("Paid $%.2f in Cash.%n", amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "Cash";
    }
}
