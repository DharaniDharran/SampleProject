package com.hotel.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private int restaurantId;
    private int addressId;
    private OrderStatus status;

    private double itemsTotal;
    private double deliveryFee;
    private double gstAmount;
    private double couponDiscount;
    private double totalAmount;

    private LocalDateTime placedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime deliveredAt;

    private final List<OrderItem> items;
    private String cancellationReason;

    public Order(int orderId, int customerId, int restaurantId, int addressId,
                 double itemsTotal, double deliveryFee, double gstAmount,
                 double couponDiscount, double totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.addressId = addressId;
        this.status = OrderStatus.PLACED;
        this.itemsTotal = itemsTotal;
        this.deliveryFee = deliveryFee;
        this.gstAmount = gstAmount;
        this.couponDiscount = couponDiscount;
        this.totalAmount = Math.max(0.0, totalAmount);
        this.placedAt = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        if (status == OrderStatus.ACCEPTED && this.acceptedAt == null) {
            this.acceptedAt = LocalDateTime.now();
        } else if (status == OrderStatus.DELIVERED && this.deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public double getItemsTotal() {
        return itemsTotal;
    }

    public void setItemsTotal(double itemsTotal) {
        this.itemsTotal = itemsTotal;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(double gstAmount) {
        this.gstAmount = gstAmount;
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = Math.max(0.0, totalAmount);
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public boolean isActive() {
        return status != OrderStatus.DELIVERED
                && status != OrderStatus.CANCELLED
                && status != OrderStatus.REJECTED;
    }

    @Override
    public String toString() {
        return String.format("Order #%d | Status: %s | Customer #%d | Restaurant #%d | Items: Rs.%.2f | Total: Rs.%.2f | Placed: %s",
                orderId, status, customerId, restaurantId, itemsTotal, totalAmount, placedAt);
    }
}
