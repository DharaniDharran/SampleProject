package com.hotel.models;

import java.util.ArrayList;
import java.util.List;

public class RestaurantOwner extends User {
    private final List<Integer> ownedRestaurantIds;

    public RestaurantOwner(int userId, String name, String phone, String email) {
        super(userId, name, phone, email, UserRole.OWNER);
        this.ownedRestaurantIds = new ArrayList<>();
    }

    public List<Integer> getOwnedRestaurantIds() {
        return ownedRestaurantIds;
    }

    public void addRestaurantId(int restaurantId) {
        if (!ownedRestaurantIds.contains(restaurantId)) {
            ownedRestaurantIds.add(restaurantId);
        }
    }

    @Override
    public String toString() {
        return String.format("RestaurantOwner[ID=%d, Name='%s', Phone='%s', OwnedRestaurants=%s]",
                getUserId(), getName(), getPhone(), ownedRestaurantIds);
    }
}
