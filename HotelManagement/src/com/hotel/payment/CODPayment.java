package com.hotel.payment;

public class CODPayment implements PaymentMethod {

    public static final double MAX_COD_LIMIT = 1500.0;

    @Override
    public boolean processPayment(double amount, String details) {
        if (amount > MAX_COD_LIMIT) {
            System.out.printf("[COD] Error: Cash on Delivery is allowed only for orders up to Rs. %.2f. Current total: Rs. %.2f%n",
                    MAX_COD_LIMIT, amount);
            return false;
        }
        System.out.printf("[COD] Cash on Delivery approved for Rs. %.2f. Payment will be collected at delivery.%n", amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "COD";
    }
}
