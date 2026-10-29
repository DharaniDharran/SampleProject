package com.hotel.models;

import java.time.LocalDate;

public class Coupon {
    private int couponId;
    private String code;
    private DiscountType discountType;
    private double discountValue;
    private double minimumOrderValue;
    private LocalDate expiryDate;

    public Coupon(int couponId, String code, DiscountType discountType, double discountValue,
                  double minimumOrderValue, LocalDate expiryDate) {
        this.couponId = couponId;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumOrderValue = minimumOrderValue;
        this.expiryDate = expiryDate;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    public double calculateDiscount(double itemsTotal) {
        if (itemsTotal < minimumOrderValue) {
            return 0.0;
        }
        if (discountType == DiscountType.PERCENTAGE) {
            return (itemsTotal * discountValue) / 100.0;
        } else {
            return Math.min(discountValue, itemsTotal);
        }
    }

    @Override
    public String toString() {
        String disc = discountType == DiscountType.PERCENTAGE ? (discountValue + "%") : ("Rs." + discountValue);
        return String.format("Coupon[%s]: %s OFF (Min Order: Rs.%.0f, Valid until: %s)",
                code, disc, minimumOrderValue, expiryDate);
    }
}
