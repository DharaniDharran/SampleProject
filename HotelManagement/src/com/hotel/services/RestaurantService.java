package com.hotel.services;

import com.hotel.models.CuisineType;
import com.hotel.models.ItemCategory;
import com.hotel.models.MenuItem;
import com.hotel.models.Restaurant;
import com.hotel.models.User;
import com.hotel.models.UserRole;
import com.hotel.repo.MenuItemRepository;
import com.hotel.repo.OrderRepository;
import com.hotel.repo.RestaurantRepository;
import com.hotel.repo.UserRepository;
import java.time.LocalTime;
import java.util.List;

public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private int nextRestaurantId = 1;
    private int nextMenuItemId = 1;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             MenuItemRepository menuItemRepository,
                             UserRepository userRepository,
                             OrderRepository orderRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public RestaurantRepository getRestaurantRepository() {
        return restaurantRepository;
    }

    public MenuItemRepository getMenuItemRepository() {
        return menuItemRepository;
    }

    public Restaurant createRestaurant(int requestingUserId, String name, CuisineType cuisineType,
                                       String city, LocalTime openingTime, LocalTime closingTime,
                                       double minimumOrderValue) {
        validateAdminOrOwner(requestingUserId);

        Restaurant restaurant = new Restaurant(nextRestaurantId++, requestingUserId, name,
                cuisineType, city, openingTime, closingTime, minimumOrderValue);
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    public void activateRestaurant(int restaurantId, int requestingUserId) {
        validateAdminOrOwner(requestingUserId);
        Restaurant r = getRestaurantOrThrow(restaurantId);
        r.setActive(true);
        r.setOpen(true);
    }

    public void toggleOpenStatus(int restaurantId, int requestingUserId) {
        validateAdminOrOwner(requestingUserId);
        Restaurant r = getRestaurantOrThrow(restaurantId);
        r.setOpen(!r.isOpen());
    }

    public void updateTimings(int restaurantId, int requestingUserId, LocalTime newOpen, LocalTime newClose) {
        validateAdminOrOwner(requestingUserId);
        Restaurant r = getRestaurantOrThrow(restaurantId);
        r.setOpeningTime(newOpen);
        r.setClosingTime(newClose);
    }

    public void updateMinimumOrderValue(int restaurantId, int requestingUserId, double newMin) {
        validateAdminOrOwner(requestingUserId);
        if (newMin < 0) {
            throw new IllegalArgumentException("Minimum order value cannot be negative.");
        }
        Restaurant r = getRestaurantOrThrow(restaurantId);
        r.setMinimumOrderValue(newMin);
    }

    public MenuItem addMenuItem(int restaurantId, int requestingUserId, String name,
                                double price, ItemCategory category, boolean isAvailable) {
        validateAdminOrOwner(requestingUserId);
        getRestaurantOrThrow(restaurantId);

        MenuItem item = new MenuItem(nextMenuItemId++, restaurantId, name, price, category, isAvailable);
        menuItemRepository.save(item);
        return item;
    }

    public void updateMenuItemPrice(int itemId, int requestingUserId, double newPrice) {
        validateAdminOrOwner(requestingUserId);
        MenuItem item = menuItemRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Menu item not found with ID: " + itemId);
        }
        item.setPrice(newPrice);
    }

    public void toggleMenuItemAvailability(int itemId, int requestingUserId) {
        validateAdminOrOwner(requestingUserId);
        MenuItem item = menuItemRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Menu item not found with ID: " + itemId);
        }
        item.setAvailable(!item.isAvailable());
    }

    public boolean deleteRestaurant(int restaurantId, int requestingUserId) {
        validateAdminOrOwner(requestingUserId);
        Restaurant r = getRestaurantOrThrow(restaurantId);

        // Module 1: A restaurant cannot be deleted if it has active orders (orders that are not yet Delivered or Cancelled/Rejected).
        int activeOrders = orderRepository.countActiveOrdersByRestaurant(restaurantId);
        if (activeOrders > 0) {
            throw new IllegalStateException("Cannot delete restaurant #" + restaurantId + ": it has " + activeOrders + " active order(s).");
        }

        // Remove menu items
        List<MenuItem> items = menuItemRepository.findByRestaurantId(restaurantId);
        for (MenuItem mi : items) {
            menuItemRepository.deleteById(mi.getItemId());
        }

        return restaurantRepository.deleteById(restaurantId);
    }

    public Restaurant getRestaurantById(int restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCity(city);
    }

    public List<Restaurant> getRestaurantsByCuisine(CuisineType cuisine) {
        return restaurantRepository.findByCuisine(cuisine);
    }

    public List<MenuItem> getMenuItemsByRestaurant(int restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public MenuItem getMenuItemById(int itemId) {
        return menuItemRepository.findById(itemId);
    }

    private void validateAdminOrOwner(int userId) {
        User user = userRepository.findById(userId);
        if (user == null || (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.OWNER)) {
            throw new SecurityException("Only an Admin or Restaurant Owner can perform this action.");
        }
    }

    private Restaurant getRestaurantOrThrow(int restaurantId) {
        Restaurant r = restaurantRepository.findById(restaurantId);
        if (r == null) {
            throw new IllegalArgumentException("Restaurant not found with ID: " + restaurantId);
        }
        return r;
    }
}
