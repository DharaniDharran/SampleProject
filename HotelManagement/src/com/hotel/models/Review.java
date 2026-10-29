package com.hotel.models;

import java.time.LocalDateTime;

public class Review {
    private int reviewId;
    private int orderId;
    private int customerId;
    private int restaurantRating; // 1 to 5
    private int deliveryRating;   // 1 to 5
    private String comment;
    private LocalDateTime createdAt;

    public Review(int reviewId, int orderId, int customerId, int restaurantRating, int deliveryRating, String comment) {
        if (restaurantRating < 1 || restaurantRating > 5) {
            throw new IllegalArgumentException("Restaurant rating must be between 1 and 5.");
        }
        if (deliveryRating < 1 || deliveryRating > 5) {
            throw new IllegalArgumentException("Delivery partner rating must be between 1 and 5.");
        }
        this.reviewId = reviewId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantRating = restaurantRating;
        this.deliveryRating = deliveryRating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getRestaurantRating() {
        return restaurantRating;
    }

    public int getDeliveryRating() {
        return deliveryRating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("Review #%d | Order #%d | Customer #%d | Restaurant: %d/5 | Delivery: %d/5 | Comment: '%s' | Time: %s",
                reviewId, orderId, customerId, restaurantRating, deliveryRating, comment, createdAt);
    }
}
