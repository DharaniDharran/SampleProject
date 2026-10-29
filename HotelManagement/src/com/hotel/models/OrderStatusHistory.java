package com.hotel.models;

import java.time.LocalDateTime;

public class OrderStatusHistory {
    private int historyId;
    private int orderId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private LocalDateTime changedAt;
    private String reason;

    public OrderStatusHistory(int historyId, int orderId, OrderStatus oldStatus, OrderStatus newStatus, String reason) {
        this.historyId = historyId;
        this.orderId = orderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = LocalDateTime.now();
        this.reason = reason;
    }

    public int getHistoryId() {
        return historyId;
    }

    public int getOrderId() {
        return orderId;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return String.format("[%s] Status changed from %s -> %s %s",
                changedAt, oldStatus != null ? oldStatus : "START", newStatus,
                (reason != null && !reason.isEmpty()) ? "(Reason: " + reason + ")" : "");
    }
}
