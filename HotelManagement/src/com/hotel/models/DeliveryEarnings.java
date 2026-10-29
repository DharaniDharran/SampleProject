package com.hotel.models;

import java.time.LocalDateTime;

public class DeliveryEarnings {
    private int earningId;
    private int partnerId;
    private int deliveryId;
    private double amount;
    private LocalDateTime earnedAt;

    public DeliveryEarnings(int earningId, int partnerId, int deliveryId, double amount) {
        this.earningId = earningId;
        this.partnerId = partnerId;
        this.deliveryId = deliveryId;
        this.amount = amount;
        this.earnedAt = LocalDateTime.now();
    }

    public int getEarningId() {
        return earningId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    @Override
    public String toString() {
        return String.format("Earning #%d | Partner #%d | Delivery #%d | Amount: Rs.%.2f | Date: %s",
                earningId, partnerId, deliveryId, amount, earnedAt);
    }
}
