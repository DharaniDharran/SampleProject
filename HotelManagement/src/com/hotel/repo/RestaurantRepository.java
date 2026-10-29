package com.hotel.repo;

import com.hotel.models.CuisineType;
import com.hotel.models.Restaurant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantRepository {
    private final Map<Integer, Restaurant> restaurants = new HashMap<>();

    public void save(Restaurant restaurant) {
        restaurants.put(restaurant.getRestaurantId(), restaurant);
    }

    public Restaurant findById(int id) {
        return restaurants.get(id);
    }

    public List<Restaurant> findAll() {
        return new ArrayList<>(restaurants.values());
    }

    public List<Restaurant> findByCity(String city) {
        List<Restaurant> list = new ArrayList<>();
        for (Restaurant r : restaurants.values()) {
            if (r.getCity().equalsIgnoreCase(city)) {
                list.add(r);
            }
        }
        return list;
    }

    public List<Restaurant> findByCuisine(CuisineType cuisine) {
        List<Restaurant> list = new ArrayList<>();
        for (Restaurant r : restaurants.values()) {
            if (r.getCuisineType() == cuisine) {
                list.add(r);
            }
        }
        return list;
    }

    public List<Restaurant> findByOwnerId(int ownerId) {
        List<Restaurant> list = new ArrayList<>();
        for (Restaurant r : restaurants.values()) {
            if (r.getOwnerId() == ownerId) {
                list.add(r);
            }
        }
        return list;
    }

    public boolean deleteById(int id) {
        return restaurants.remove(id) != null;
    }
}
