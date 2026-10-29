package com.rest.Payment;

public class CardPayment implements PaymentMethod {

    @Override
    public boolean pay(double amount) {
        System.out.printf("Paid $%.2f via Credit/Debit Card.%n", amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "Card";
    }
}
