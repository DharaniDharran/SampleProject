package com.hotel.models;

import java.time.LocalDateTime;

public class Delivery {
    private int deliveryId;
    private int orderId;
    private int partnerId;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    public Delivery(int deliveryId, int orderId, int partnerId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.partnerId = partnerId;
        this.status = DeliveryStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
        if (status == DeliveryStatus.PICKED_UP && this.pickedUpAt == null) {
            this.pickedUpAt = LocalDateTime.now();
        } else if (status == DeliveryStatus.DELIVERED && this.deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    @Override
    public String toString() {
        return String.format("Delivery #%d | Order #%d | Partner #%d | Status: %s | Assigned: %s",
                deliveryId, orderId, partnerId, status, assignedAt);
    }
}
