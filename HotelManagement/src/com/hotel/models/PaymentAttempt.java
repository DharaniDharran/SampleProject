package com.hotel.models;

import java.time.LocalDateTime;

public class PaymentAttempt {
    private int attemptId;
    private int paymentId;
    private int attemptNumber;
    private String status;
    private LocalDateTime attemptTime;

    public PaymentAttempt(int attemptId, int paymentId, int attemptNumber, String status) {
        this.attemptId = attemptId;
        this.paymentId = paymentId;
        this.attemptNumber = attemptNumber;
        this.status = status;
        this.attemptTime = LocalDateTime.now();
    }

    public int getAttemptId() {
        return attemptId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }
}
