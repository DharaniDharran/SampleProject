package com.hotel.repo;

import com.hotel.models.MenuItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuItemRepository {
    private final Map<Integer, MenuItem> items = new HashMap<>();

    public void save(MenuItem item) {
        items.put(item.getItemId(), item);
    }

    public MenuItem findById(int id) {
        return items.get(id);
    }

    public List<MenuItem> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<MenuItem> findByRestaurantId(int restaurantId) {
        List<MenuItem> list = new ArrayList<>();
        for (MenuItem item : items.values()) {
            if (item.getRestaurantId() == restaurantId) {
                list.add(item);
            }
        }
        return list;
    }

    public boolean deleteById(int id) {
        return items.remove(id) != null;
    }
}
