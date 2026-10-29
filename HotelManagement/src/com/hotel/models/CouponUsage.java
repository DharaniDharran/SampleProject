package com.hotel.models;

import java.time.LocalDateTime;

public class CouponUsage {
    private int usageId;
    private int couponId;
    private int customerId;
    private int orderId;
    private LocalDateTime usedAt;

    public CouponUsage(int usageId, int couponId, int customerId, int orderId) {
        this.usageId = usageId;
        this.couponId = couponId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.usedAt = LocalDateTime.now();
    }

    public int getUsageId() {
        return usageId;
    }

    public int getCouponId() {
        return couponId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }
}
