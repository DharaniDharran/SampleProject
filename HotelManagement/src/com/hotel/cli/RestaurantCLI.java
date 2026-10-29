package com.hotel.cli;

import com.hotel.models.CuisineType;
import com.hotel.models.ItemCategory;
import com.hotel.models.MenuItem;
import com.hotel.models.Order;
import com.hotel.models.OrderStatus;
import com.hotel.models.Restaurant;
import com.hotel.models.RestaurantOwner;
import com.hotel.models.User;
import com.hotel.models.UserRole;
import com.hotel.services.OrderService;
import com.hotel.services.RestaurantService;
import com.hotel.services.UserService;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class RestaurantCLI {
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final UserService userService;
    private final Scanner scanner;

    private RestaurantOwner activeOwner;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public RestaurantCLI(RestaurantService restaurantService,
                         OrderService orderService,
                         UserService userService,
                         Scanner scanner) {
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.userService = userService;
        this.scanner = scanner;
    }

    public void setActiveOwner(RestaurantOwner owner) {
        this.activeOwner = owner;
    }

    public void start() {
        if (activeOwner == null) {
            selectOrCreateOwner();
        }

        while (scanner.hasNextLine()) {
            System.out.println("\n==================================");
            System.out.printf("   RESTAURANT PORTAL: %s (ID: %d)%n", activeOwner.getName(), activeOwner.getUserId());
            System.out.println("==================================");
            System.out.println("1. Add New Restaurant");
            System.out.println("2. View All Restaurants & Menus");
            System.out.println("3. Activate / Toggle Open-Close Restaurant");
            System.out.println("4. Add / Update Menu Item");
            System.out.println("5. Toggle Menu Item Availability");
            System.out.println("6. View Incoming Orders for My Restaurant");
            System.out.println("7. Accept an Order (within 5 mins)");
            System.out.println("8. Reject an Order (with reason)");
            System.out.println("9. Progress Order Status (Preparing -> Ready for Pickup)");
            System.out.println("10. Delete a Restaurant");
            System.out.println("11. Switch Owner");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter Choice: ");

            String choiceStr = scanner.nextLine().trim();
            if (choiceStr.isEmpty()) continue;
            int choice = -1;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        addRestaurantUI();
                        break;
                    case 2:
                        viewRestaurantsUI();
                        break;
                    case 3:
                        toggleOpenCloseUI();
                        break;
                    case 4:
                        menuItemUI();
                        break;
                    case 5:
                        toggleItemAvailabilityUI();
                        break;
                    case 6:
                        viewIncomingOrdersUI();
                        break;
                    case 7:
                        acceptOrderUI();
                        break;
                    case 8:
                        rejectOrderUI();
                        break;
                    case 9:
                        progressOrderStatusUI();
                        break;
                    case 10:
                        deleteRestaurantUI();
                        break;
                    case 11:
                        selectOrCreateOwner();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select an option from the menu.");
                }
            } catch (Exception ex) {
                System.out.println("\n[ERROR] " + ex.getMessage());
            }
        }
    }

    private void selectOrCreateOwner() {
        System.out.println("\n--- Select / Register Restaurant Owner ---");
        List<User> owners = userService.getUsersByRole(UserRole.OWNER);
        if (!owners.isEmpty()) {
            System.out.println("Existing Restaurant Owners:");
            for (User u : owners) {
                System.out.printf("  [%d] %s (%s)%n", u.getUserId(), u.getName(), u.getPhone());
            }
            System.out.println("  [0] Register a New Owner");
            System.out.print("Select Owner ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (id > 0) {
                User u = userService.getUserById(id);
                if (u instanceof RestaurantOwner) {
                    activeOwner = (RestaurantOwner) u;
                    System.out.println("Logged in as " + activeOwner.getName());
                    return;
                }
            }
        }

        System.out.print("Enter Owner Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        activeOwner = userService.registerOwner(name, phone, email);
        System.out.println("Owner registered successfully with ID: " + activeOwner.getUserId());
    }

    private void addRestaurantUI() {
        System.out.println("\n--- Add New Restaurant ---");
        System.out.print("Enter Restaurant Name: ");
        String name = scanner.nextLine().trim();

        System.out.println("Select Cuisine Type:");
        for (CuisineType c : CuisineType.values()) {
            System.out.println(" - " + c.getDisplayName());
        }
        System.out.print("Enter Cuisine Type: ");
        String cuisineStr = scanner.nextLine().trim();
        CuisineType cuisine = CuisineType.fromString(cuisineStr);
        if (cuisine == null) {
            System.out.println("Invalid cuisine type.");
            return;
        }

        System.out.print("Enter City (e.g. Bangalore / Mumbai): ");
        String city = scanner.nextLine().trim();

        System.out.print("Enter Opening Time (HH:mm, e.g. 09:00): ");
        LocalTime openTime = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMAT);

        System.out.print("Enter Closing Time (HH:mm, e.g. 23:00): ");
        LocalTime closeTime = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMAT);

        System.out.print("Enter Minimum Order Value (Rs.): ");
        double minOrder = Double.parseDouble(scanner.nextLine().trim());

        Restaurant r = restaurantService.createRestaurant(activeOwner.getUserId(), name, cuisine, city, openTime, closeTime, minOrder);
        activeOwner.addRestaurantId(r.getRestaurantId());
        restaurantService.activateRestaurant(r.getRestaurantId(), activeOwner.getUserId());

        System.out.printf("Restaurant '%s' created and activated with ID #%d!%n", r.getName(), r.getRestaurantId());
    }

    private void viewRestaurantsUI() {
        List<Restaurant> list = restaurantService.getAllRestaurants();
        System.out.println("\n=== RESTAURANTS & MENUS ===");
        if (list.isEmpty()) {
            System.out.println("No restaurants found.");
            return;
        }
        for (Restaurant r : list) {
            System.out.println(r);
            List<MenuItem> items = restaurantService.getMenuItemsByRestaurant(r.getRestaurantId());
            if (items.isEmpty()) {
                System.out.println("   (No menu items yet)");
            } else {
                for (MenuItem mi : items) {
                    System.out.println("   --> " + mi);
                }
            }
        }
    }

    private void toggleOpenCloseUI() {
        System.out.print("Enter Restaurant ID to toggle Open/Closed: ");
        int rId = Integer.parseInt(scanner.nextLine().trim());
        restaurantService.toggleOpenStatus(rId, activeOwner.getUserId());
        Restaurant r = restaurantService.getRestaurantById(rId);
        System.out.printf("Restaurant '%s' is now %s.%n", r.getName(), r.isOpen() ? "OPEN" : "CLOSED");
    }

    private void menuItemUI() {
        System.out.print("Enter Restaurant ID to add item to: ");
        int rId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Item Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Price (must be > 0): ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Category (Veg / Non-Veg): ");
        String catStr = scanner.nextLine().trim();
        ItemCategory category = ItemCategory.fromString(catStr);
        if (category == null) category = ItemCategory.VEG;

        MenuItem mi = restaurantService.addMenuItem(rId, activeOwner.getUserId(), name, price, category, true);
        System.out.println("Created menu item: " + mi);
    }

    private void toggleItemAvailabilityUI() {
        System.out.print("Enter Menu Item ID to toggle availability: ");
        int itemId = Integer.parseInt(scanner.nextLine().trim());
        restaurantService.toggleMenuItemAvailability(itemId, activeOwner.getUserId());
        MenuItem item = restaurantService.getMenuItemById(itemId);
        System.out.printf("Item '%s' is now %s.%n", item.getName(), item.isAvailable() ? "AVAILABLE" : "UNAVAILABLE");
    }

    private void viewIncomingOrdersUI() {
        System.out.print("Enter Restaurant ID to view orders: ");
        int rId = Integer.parseInt(scanner.nextLine().trim());
        List<Order> orders = orderService.getOrdersByRestaurant(rId);
        System.out.printf("%n=== INCOMING ORDERS FOR RESTAURANT #%d ===%n", rId);
        if (orders.isEmpty()) {
            System.out.println("No orders for this restaurant.");
            return;
        }
        for (Order o : orders) {
            System.out.println(o);
        }
    }

    private void acceptOrderUI() {
        System.out.print("Enter Order ID to accept: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());
        orderService.acceptOrder(orderId, activeOwner.getUserId());
    }

    private void rejectOrderUI() {
        System.out.print("Enter Order ID to reject: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Rejection Reason: ");
        String reason = scanner.nextLine().trim();
        orderService.rejectOrder(orderId, activeOwner.getUserId(), reason);
    }

    private void progressOrderStatusUI() {
        System.out.print("Enter Order ID to progress: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        System.out.printf("Current Status: %s%n", order.getStatus());
        System.out.println("Available Next Stages:");
        if (order.getStatus() == OrderStatus.ACCEPTED) {
            System.out.println(" 1. Move to PREPARING");
        } else if (order.getStatus() == OrderStatus.PREPARING) {
            System.out.println(" 2. Move to READY_FOR_PICKUP (Triggers Delivery Partner Assignment)");
        } else {
            System.out.println(" (Cannot progress from this status in Restaurant Portal)");
            return;
        }

        System.out.print("Enter choice: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        if (choice == 1 && order.getStatus() == OrderStatus.ACCEPTED) {
            orderService.progressOrderStatus(orderId, OrderStatus.PREPARING, activeOwner.getUserId());
        } else if (choice == 2 && order.getStatus() == OrderStatus.PREPARING) {
            orderService.progressOrderStatus(orderId, OrderStatus.READY_FOR_PICKUP, activeOwner.getUserId());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void deleteRestaurantUI() {
        System.out.print("Enter Restaurant ID to delete: ");
        int rId = Integer.parseInt(scanner.nextLine().trim());
        restaurantService.deleteRestaurant(rId, activeOwner.getUserId());
        System.out.println("Restaurant #" + rId + " deleted successfully.");
    }
}
