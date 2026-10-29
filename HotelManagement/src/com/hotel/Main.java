package com.hotel;

import com.hotel.cli.AdminCLI;
import com.hotel.cli.CustomerCLI;
import com.hotel.cli.DeliveryCLI;
import com.hotel.cli.DemoSimulation;
import com.hotel.cli.RestaurantCLI;
import com.hotel.models.Admin;
import com.hotel.models.CuisineType;
import com.hotel.models.Customer;
import com.hotel.models.DeliveryPartner;
import com.hotel.models.DiscountType;
import com.hotel.models.ItemCategory;
import com.hotel.models.Restaurant;
import com.hotel.models.RestaurantOwner;
import com.hotel.repo.CartRepository;
import com.hotel.repo.DeliveryRepository;
import com.hotel.repo.MenuItemRepository;
import com.hotel.repo.OrderRepository;
import com.hotel.repo.PaymentRepository;
import com.hotel.repo.RestaurantRepository;
import com.hotel.repo.ReviewRepository;
import com.hotel.repo.UserRepository;
import com.hotel.services.CartService;
import com.hotel.services.DeliveryService;
import com.hotel.services.OrderService;
import com.hotel.services.PaymentService;
import com.hotel.services.RestaurantService;
import com.hotel.services.ReviewService;
import com.hotel.services.UserService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Initialize Repositories
        UserRepository userRepo = new UserRepository();
        RestaurantRepository restRepo = new RestaurantRepository();
        MenuItemRepository menuRepo = new MenuItemRepository();
        CartRepository cartRepo = new CartRepository();
        OrderRepository orderRepo = new OrderRepository();
        DeliveryRepository deliveryRepo = new DeliveryRepository();
        PaymentRepository paymentRepo = new PaymentRepository();
        ReviewRepository reviewRepo = new ReviewRepository();

        // 2. Initialize Services
        UserService userService = new UserService(userRepo);
        RestaurantService restaurantService = new RestaurantService(restRepo, menuRepo, userRepo, orderRepo);
        CartService cartService = new CartService(cartRepo, menuRepo);
        PaymentService paymentService = new PaymentService(paymentRepo);
        OrderService orderService = new OrderService(orderRepo, restRepo, userRepo, cartService, paymentService);
        DeliveryService deliveryService = new DeliveryService(deliveryRepo, userRepo, restRepo, orderRepo, paymentRepo);
        orderService.setDeliveryService(deliveryService);
        ReviewService reviewService = new ReviewService(reviewRepo, orderRepo, restRepo);

        // 3. Seed Initial Data
        seedInitialData(userService, restaurantService, paymentService);

        // 4. Initialize CLI Modules
        CustomerCLI customerCLI = new CustomerCLI(userService, restaurantService, cartService, orderService, paymentService, reviewService, scanner);
        RestaurantCLI restaurantCLI = new RestaurantCLI(restaurantService, orderService, userService, scanner);
        DeliveryCLI deliveryCLI = new DeliveryCLI(deliveryService, orderService, userService, scanner);
        AdminCLI adminCLI = new AdminCLI(userService, restaurantService, orderService, deliveryService, paymentService, scanner);

        // Pre-select default customer, owner, and partner for quick convenience
        customerCLI.setActiveCustomer((Customer) userService.getUserById(4)); // Alice
        restaurantCLI.setActiveOwner((RestaurantOwner) userService.getUserById(2)); // Suresh
        deliveryCLI.setActivePartner((DeliveryPartner) userService.getUserById(7)); // Rajesh

        // 5. Main CLI Loop
        while (scanner.hasNextLine()) {
            System.out.println("\n========================================================");
            System.out.println("            ONLINE FOOD DELIVERY PLATFORM               ");
            System.out.println("              (Low-Level System Design)                 ");
            System.out.println("========================================================");
            System.out.println("1. Customer Portal (Browse, Cart, Order, Cancel, Rate)");
            System.out.println("2. Restaurant Portal (Menu, Orders, Accept, Preparing, Ready)");
            System.out.println("3. Delivery Partner Portal (View Deliveries, Accept/Reject, Deliver)");
            System.out.println("4. Admin Portal (Overview, Flagged Restaurants, Queues, Coupons)");
            System.out.println("5. Run Automated End-to-End Demo Simulation");
            System.out.println("0. Exit Application");
            System.out.println("--------------------------------------------------------");
            System.out.print("Enter Choice: ");

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue;
            }

            if (choice == 1) {
                customerCLI.start();
            } else if (choice == 2) {
                restaurantCLI.start();
            } else if (choice == 3) {
                deliveryCLI.start();
            } else if (choice == 4) {
                adminCLI.start();
            } else if (choice == 5) {
                DemoSimulation.runAutomatedDemo(userService, restaurantService, cartService,
                        orderService, deliveryService, paymentService, reviewService, adminCLI);
            } else if (choice == 0) {
                System.out.println("\nThank you for using Online Food Delivery Platform. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please choose from 0 to 5.");
            }
        }

        scanner.close();
    }

    private static void seedInitialData(UserService userService,
                                       RestaurantService restaurantService,
                                       PaymentService paymentService) {
        // Users
        Admin admin = userService.registerAdmin("Vikram (Admin)", "9999900001", "admin@foodplatform.com");
        RestaurantOwner owner1 = userService.registerOwner("Suresh Gupta", "9888800002", "suresh@spicegarden.com");
        RestaurantOwner owner2 = userService.registerOwner("Meera Rao", "9777700003", "meera@wok.com");

        Customer cust1 = userService.registerCustomer("Alice Sharma", "9666600004", "alice@example.com");
        userService.addCustomerAddress(cust1.getUserId(), "Bangalore", "Flat 402, Green Acres, Indiranagar", "560038");

        Customer cust2 = userService.registerCustomer("Bob Fernandes", "9555500005", "bob@example.com");
        userService.addCustomerAddress(cust2.getUserId(), "Mumbai", "12 Seaside Heights, Bandra", "400050");

        Customer cust3 = userService.registerCustomer("Charlie Verma", "9444400006", "charlie@example.com");
        userService.addCustomerAddress(cust3.getUserId(), "Bangalore", "B-103 Palm Meadows, Whitefield", "560066");

        DeliveryPartner dp1 = userService.registerDeliveryPartner("Rajesh Kumar", "9333300007", "rajesh@delivery.com", "Bangalore");
        DeliveryPartner dp2 = userService.registerDeliveryPartner("Ramesh Gowda", "9222200008", "ramesh@delivery.com", "Bangalore");
        DeliveryPartner dp3 = userService.registerDeliveryPartner("Priya Shinde", "9111100009", "priya@delivery.com", "Mumbai");

        // Restaurants
        // Restaurant 1: Spice Garden (Bangalore, North Indian)
        Restaurant r1 = restaurantService.createRestaurant(owner1.getUserId(), "Spice Garden",
                CuisineType.NORTH_INDIAN, "Bangalore", LocalTime.of(8, 0), LocalTime.of(23, 30), 200.0);
        restaurantService.activateRestaurant(r1.getRestaurantId(), owner1.getUserId());
        owner1.addRestaurantId(r1.getRestaurantId());

        restaurantService.addMenuItem(r1.getRestaurantId(), owner1.getUserId(), "Butter Chicken", 320.0, ItemCategory.NON_VEG, true);
        restaurantService.addMenuItem(r1.getRestaurantId(), owner1.getUserId(), "Paneer Tikka", 240.0, ItemCategory.VEG, true);
        restaurantService.addMenuItem(r1.getRestaurantId(), owner1.getUserId(), "Garlic Naan", 60.0, ItemCategory.VEG, true);

        // Restaurant 2: Dosa Hut (Bangalore, South Indian)
        Restaurant r2 = restaurantService.createRestaurant(owner1.getUserId(), "Dosa Hut",
                CuisineType.SOUTH_INDIAN, "Bangalore", LocalTime.of(7, 0), LocalTime.of(22, 0), 150.0);
        restaurantService.activateRestaurant(r2.getRestaurantId(), owner1.getUserId());
        owner1.addRestaurantId(r2.getRestaurantId());

        restaurantService.addMenuItem(r2.getRestaurantId(), owner1.getUserId(), "Masala Dosa", 120.0, ItemCategory.VEG, true);
        restaurantService.addMenuItem(r2.getRestaurantId(), owner1.getUserId(), "Idli Vada Combo", 90.0, ItemCategory.VEG, true);
        restaurantService.addMenuItem(r2.getRestaurantId(), owner1.getUserId(), "Filter Coffee", 40.0, ItemCategory.VEG, true);

        // Restaurant 3: Dragon Wok (Mumbai, Chinese)
        Restaurant r3 = restaurantService.createRestaurant(owner2.getUserId(), "Dragon Wok",
                CuisineType.CHINESE, "Mumbai", LocalTime.of(11, 0), LocalTime.of(23, 30), 250.0);
        restaurantService.activateRestaurant(r3.getRestaurantId(), owner2.getUserId());
        owner2.addRestaurantId(r3.getRestaurantId());

        restaurantService.addMenuItem(r3.getRestaurantId(), owner2.getUserId(), "Hakka Noodles", 220.0, ItemCategory.VEG, true);
        restaurantService.addMenuItem(r3.getRestaurantId(), owner2.getUserId(), "Chilli Chicken", 280.0, ItemCategory.NON_VEG, true);

        // Restaurant 4: Tandoori Nights (Bangalore, North Indian)
        Restaurant r4 = restaurantService.createRestaurant(owner1.getUserId(), "Tandoori Nights",
                CuisineType.NORTH_INDIAN, "Bangalore", LocalTime.of(8, 0), LocalTime.of(23, 0), 200.0);
        restaurantService.activateRestaurant(r4.getRestaurantId(), owner1.getUserId());
        owner1.addRestaurantId(r4.getRestaurantId());
        restaurantService.addMenuItem(r4.getRestaurantId(), owner1.getUserId(), "Tandoori Chicken Full", 450.0, ItemCategory.NON_VEG, true);
        restaurantService.addMenuItem(r4.getRestaurantId(), owner1.getUserId(), "Dal Makhani", 210.0, ItemCategory.VEG, true);

        // Restaurant 5: Delhi Chaat Bhandar (Bangalore, North Indian)
        Restaurant r5 = restaurantService.createRestaurant(owner1.getUserId(), "Delhi Chaat Bhandar",
                CuisineType.NORTH_INDIAN, "Bangalore", LocalTime.of(10, 0), LocalTime.of(22, 0), 100.0);
        restaurantService.activateRestaurant(r5.getRestaurantId(), owner1.getUserId());
        owner1.addRestaurantId(r5.getRestaurantId());
        restaurantService.addMenuItem(r5.getRestaurantId(), owner1.getUserId(), "Dahi Puri", 90.0, ItemCategory.VEG, true);
        restaurantService.addMenuItem(r5.getRestaurantId(), owner1.getUserId(), "Chole Bhature", 150.0, ItemCategory.VEG, true);

        // Coupons
        paymentService.addCoupon("WELCOME50", DiscountType.FLAT, 50.0, 200.0, LocalDate.now().plusMonths(2));
        paymentService.addCoupon("FLAT100", DiscountType.FLAT, 100.0, 500.0, LocalDate.now().plusMonths(3));
        paymentService.addCoupon("FESTIVE20", DiscountType.PERCENTAGE, 20.0, 300.0, LocalDate.now().plusMonths(1));
    }
}
