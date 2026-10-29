package com.hotel.models;

public enum CuisineType {
    NORTH_INDIAN("North Indian"),
    SOUTH_INDIAN("South Indian"),
    CHINESE("Chinese"),
    ITALIAN("Italian"),
    FAST_FOOD("Fast Food");

    private final String displayName;

    CuisineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CuisineType fromString(String text) {
        for (CuisineType c : CuisineType.values()) {
            if (c.displayName.equalsIgnoreCase(text) || c.name().equalsIgnoreCase(text.replace(" ", "_"))) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
