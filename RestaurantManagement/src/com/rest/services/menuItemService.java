package com.rest.services;

import com.rest.models.menuItem;
import com.rest.repository.menuItemRepository;
import java.util.ArrayList;
import java.util.List;

public class menuItemService {
    private final menuItemRepository repository;

    public menuItemService() {
        this.repository = new menuItemRepository();
    }

    public menuItemService(menuItemRepository repository) {
        this.repository = repository;
    }

    public List<menuItem> getAllMenuItems() {
        return repository.findAll();
    }

    public menuItem getMenuItemById(int id) {
        return repository.findById(id);
    }

    public void addMenuItem(menuItem item) {
        repository.save(item);
    }

    public List<menuItem> getMenuItemsByCategory(String category) {
        List<menuItem> filtered = new ArrayList<>();
        for (menuItem item : repository.findAll()) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                filtered.add(item);
            }
        }
        return filtered;
    }
}
