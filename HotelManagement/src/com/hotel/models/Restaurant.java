package com.hotel.models;

import java.time.LocalTime;

public class Restaurant {
    private int restaurantId;
    private int ownerId;
    private String name;
    private CuisineType cuisineType;
    private String city;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private double minimumOrderValue;
    private boolean isOpen;
    private boolean isActive;

    // Rating stats
    private double totalRatingSum;
    private int ratingCount;
    private double averageRating;
    private boolean flaggedForReview;

    public Restaurant(int restaurantId, int ownerId, String name, CuisineType cuisineType,
                      String city, LocalTime openingTime, LocalTime closingTime,
                      double minimumOrderValue) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name is required.");
        }
        if (cuisineType == null) {
            throw new IllegalArgumentException("Cuisine type is required.");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required.");
        }
        if (openingTime == null || closingTime == null) {
            throw new IllegalArgumentException("Opening and closing times are required.");
        }
        if (minimumOrderValue < 0) {
            throw new IllegalArgumentException("Minimum order value cannot be negative.");
        }

        this.restaurantId = restaurantId;
        this.ownerId = ownerId;
        this.name = name;
        this.cuisineType = cuisineType;
        this.city = city;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.minimumOrderValue = minimumOrderValue;
        this.isOpen = false;
        this.isActive = false;
        this.totalRatingSum = 0.0;
        this.ratingCount = 0;
        this.averageRating = 0.0;
        this.flaggedForReview = false;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isActive) {
            throw new IllegalStateException("Activated restaurant's name cannot be changed.");
        }
        this.name = name;
    }

    public CuisineType getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(CuisineType cuisineType) {
        if (isActive) {
            throw new IllegalStateException("Activated restaurant's cuisine type cannot be changed.");
        }
        this.cuisineType = cuisineType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (isActive) {
            throw new IllegalStateException("Activated restaurant's city cannot be changed.");
        }
        this.city = city;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public boolean isFlaggedForReview() {
        return flaggedForReview;
    }

    public void addRating(int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5.");
        }
        this.totalRatingSum += score;
        this.ratingCount++;
        this.averageRating = Math.round((totalRatingSum / ratingCount) * 100.0) / 100.0;

        // Module 6: A restaurant with an average rating below 2.0 after at least 10 ratings is flagged for admin review.
        if (this.ratingCount >= 10 && this.averageRating < 2.0) {
            this.flaggedForReview = true;
        } else {
            this.flaggedForReview = false;
        }
    }

    public boolean isWithinOpeningHours(LocalTime time) {
        if (time == null) return false;
        if (openingTime.isBefore(closingTime)) {
            return !time.isBefore(openingTime) && !time.isAfter(closingTime);
        } else {
            // Handles overnight operating hours (e.g., 20:00 to 02:00)
            return !time.isBefore(openingTime) || !time.isAfter(closingTime);
        }
    }

    public boolean canAcceptOrders(LocalTime currentTime) {
        return isOpen && isWithinOpeningHours(currentTime);
    }

    @Override
    public String toString() {
        String ratingStr = ratingCount > 0 ? String.format("%.1f (%d reviews)", averageRating, ratingCount) : "No ratings yet";
        return String.format("[#%d] %s | Cuisine: %s | City: %s | Hours: %s-%s | Min Order: Rs.%.0f | Status: %s | Rating: %s %s",
                restaurantId, name, cuisineType, city, openingTime, closingTime, minimumOrderValue,
                isOpen ? "OPEN" : "CLOSED", ratingStr, flaggedForReview ? "[FLAGGED FOR REVIEW]" : "");
    }
}
