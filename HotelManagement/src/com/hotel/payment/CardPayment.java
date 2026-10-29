package com.hotel.payment;

public class CardPayment implements PaymentMethod {

    @Override
    public boolean processPayment(double amount, String cardDetails) {
        // Simple card validation simulation (cardNumber,cvv or 16 digits)
        if (cardDetails == null || cardDetails.replace(" ", "").length() < 12) {
            System.out.println("[Card] Invalid Card details. Must provide valid card number.");
            return false;
        }
        String maskedCard = cardDetails.trim();
        if (maskedCard.length() > 4) {
            maskedCard = "XXXX-XXXX-XXXX-" + maskedCard.substring(maskedCard.length() - 4);
        }
        System.out.printf("[Card] Payment of Rs.%.2f authorized via Card (%s)... SUCCESS.%n", amount, maskedCard);
        return true;
    }

    @Override
    public String getMethodName() {
        return "Card";
    }
}
