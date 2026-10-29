package com.hotel.models;

public enum OrderStatus {
    PLACED(1),
    ACCEPTED(2),
    PREPARING(3),
    READY_FOR_PICKUP(4),
    OUT_FOR_DELIVERY(5),
    DELIVERED(6),
    CANCELLED(99),
    REJECTED(100);

    private final int sequence;

    OrderStatus(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    public boolean canTransitionTo(OrderStatus next) {
        if (this == DELIVERED || this == CANCELLED || this == REJECTED) {
            return false; // Final states
        }
        if (next == CANCELLED) {
            // Customer can cancel only while Placed or Accepted
            return this == PLACED || this == ACCEPTED;
        }
        if (next == REJECTED) {
            // Restaurant can reject only while Placed
            return this == PLACED;
        }
        // Normal progression must be strictly next sequential step
        return next.sequence == this.sequence + 1;
    }
}
