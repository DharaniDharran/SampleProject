package com.hotel.cli;

import com.hotel.models.DiscountType;
import com.hotel.models.Order;
import com.hotel.models.Payment;
import com.hotel.models.Restaurant;
import com.hotel.models.User;
import com.hotel.services.DeliveryService;
import com.hotel.services.OrderService;
import com.hotel.services.PaymentService;
import com.hotel.services.RestaurantService;
import com.hotel.services.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class AdminCLI {
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final PaymentService paymentService;
    private final Scanner scanner;

    public AdminCLI(UserService userService,
                    RestaurantService restaurantService,
                    OrderService orderService,
                    DeliveryService deliveryService,
                    PaymentService paymentService,
                    Scanner scanner) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.deliveryService = deliveryService;
        this.paymentService = paymentService;
        this.scanner = scanner;
    }

    public void start() {
        while (scanner.hasNextLine()) {
            System.out.println("\n==================================");
            System.out.println("   ADMIN DASHBOARD                ");
            System.out.println("==================================");
            System.out.println("1. View All System Overview (Users, Restaurants, Orders)");
            System.out.println("2. View Flagged Restaurants (Rating < 2.0 after 10+ reviews)");
            System.out.println("3. View Delivery Queues by City");
            System.out.println("4. Add New Discount Coupon");
            System.out.println("5. View All Payments & Refunds");
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
                        viewSystemOverview();
                        break;
                    case 2:
                        viewFlaggedRestaurants();
                        break;
                    case 3:
                        viewDeliveryQueues();
                        break;
                    case 4:
                        addCouponUI();
                        break;
                    case 5:
                        viewPayments();
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

    private void viewSystemOverview() {
        System.out.println("\n=== ALL USERS ===");
        for (User u : userService.getAllUsers()) {
            System.out.println(u);
        }

        System.out.println("\n=== ALL RESTAURANTS ===");
        for (Restaurant r : restaurantService.getAllRestaurants()) {
            System.out.println(r);
        }

        System.out.println("\n=== ALL ORDERS ===");
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders in the system yet.");
        } else {
            for (Order o : orders) {
                System.out.println(o);
            }
        }
    }

    private void viewFlaggedRestaurants() {
        System.out.println("\n=== RESTAURANTS FLAGGED FOR ADMIN REVIEW ===");
        List<Restaurant> list = restaurantService.getAllRestaurants();
        boolean found = false;
        for (Restaurant r : list) {
            if (r.isFlaggedForReview() || (r.getRatingCount() >= 10 && r.getAverageRating() < 2.0)) {
                System.out.printf("[FLAGGED] Restaurant #%d: '%s' | Rating: %.2f (%d reviews)%n",
                        r.getRestaurantId(), r.getName(), r.getAverageRating(), r.getRatingCount());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No restaurants currently flagged for review.");
        }
    }

    private void viewDeliveryQueues() {
        System.out.println("\n=== DELIVERY QUEUES (ORDERS WAITING FOR PARTNER) ===");
        String[] cities = {"Bangalore", "Mumbai", "Delhi", "Hyderabad"};
        for (String city : cities) {
            Queue<Integer> q = deliveryService.getDeliveryRepository().getQueueForCity(city);
            System.out.printf("City: %-12s | Waiting Orders in Queue: %s%n", city, q.toString());
        }
    }

    private void addCouponUI() {
        System.out.print("Enter Coupon Code (e.g. MEAL50): ");
        String code = scanner.nextLine().trim();
        System.out.print("Discount Type (1 for Percentage, 2 for Flat): ");
        int typeChoice = Integer.parseInt(scanner.nextLine().trim());
        DiscountType type = typeChoice == 1 ? DiscountType.PERCENTAGE : DiscountType.FLAT;

        System.out.print("Enter Discount Value (e.g. 20 for 20% or 100 for Rs.100): ");
        double val = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Enter Minimum Order Value: ");
        double minOrder = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Enter Valid Days from Today (e.g. 30): ");
        int days = Integer.parseInt(scanner.nextLine().trim());

        paymentService.addCoupon(code, type, val, minOrder, LocalDate.now().plusDays(days));
        System.out.println("Coupon created successfully!");
    }

    private void viewPayments() {
        System.out.println("\n=== ALL PAYMENT TRANSACTIONS ===");
        List<Payment> payments = paymentService.getPaymentRepository().findAllPayments();
        if (payments.isEmpty()) {
            System.out.println("No payments recorded yet.");
            return;
        }
        for (Payment p : payments) {
            System.out.println(p);
        }
    }
}
