package com.rest.repository;

import com.rest.models.menuItem;
import java.util.ArrayList;
import java.util.List;

public class menuItemRepository {
    private final List<menuItem> menuItems = new ArrayList<>();

    public menuItemRepository() {
        menuItems.add(new menuItem(1, "Paneer Butter Masala", 250.0, "Main Course", true));
        menuItems.add(new menuItem(2, "Veg Biryani", 200.0, "Rice", true));
        menuItems.add(new menuItem(3, "Chicken Biryani", 280.0, "Rice", false));
        menuItems.add(new menuItem(4, "Crispy Corn", 160.0, "Starters", true));
        menuItems.add(new menuItem(5, "Chicken Tikka", 260.0, "Starters", false));
        menuItems.add(new menuItem(6, "Cold Coffee", 90.0, "Beverages", true));
    }

    public List<menuItem> findAll() {
        return menuItems;
    }

    public menuItem findById(int id) {
        for (menuItem item : menuItems) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public void save(menuItem item) {
        menuItems.add(item);
    }
}
