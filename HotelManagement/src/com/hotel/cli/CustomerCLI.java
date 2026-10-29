package com.hotel.cli;

import com.hotel.models.Address;
import com.hotel.models.Cart;
import com.hotel.models.CartItem;
import com.hotel.models.Customer;
import com.hotel.models.MenuItem;
import com.hotel.models.Order;
import com.hotel.models.OrderStatusHistory;
import com.hotel.models.Restaurant;
import com.hotel.services.CartService;
import com.hotel.services.OrderService;
import com.hotel.services.PaymentService;
import com.hotel.services.RestaurantService;
import com.hotel.services.ReviewService;
import com.hotel.services.UserService;
import java.util.List;
import java.util.Scanner;

public class CustomerCLI {
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final Scanner scanner;

    private Customer activeCustomer;

    public CustomerCLI(UserService userService,
                       RestaurantService restaurantService,
                       CartService cartService,
                       OrderService orderService,
                       PaymentService paymentService,
                       ReviewService reviewService,
                       Scanner scanner) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
        this.scanner = scanner;
    }

    public void setActiveCustomer(Customer customer) {
        this.activeCustomer = customer;
    }

    public void start() {
        if (activeCustomer == null) {
            selectOrCreateCustomer();
        }

        while (scanner.hasNextLine()) {
            System.out.println("\n==================================");
            System.out.printf("   CUSTOMER PORTAL: %s (ID: %d)%n", activeCustomer.getName(), activeCustomer.getUserId());
            System.out.println("==================================");
            System.out.println("1. Browse Restaurants & Menus");
            System.out.println("2. Add Item to Cart");
            System.out.println("3. View Cart & Subtotal");
            System.out.println("4. Clear Cart");
            System.out.println("5. Checkout & Place Order");
            System.out.println("6. View My Orders & Tracking");
            System.out.println("7. Cancel an Order");
            System.out.println("8. Rate & Review a Delivered Order");
            System.out.println("9. Add New Delivery Address");
            System.out.println("10. Switch / Register Another Customer");
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
                        browseRestaurants();
                        break;
                    case 2:
                        addItemToCartUI();
                        break;
                    case 3:
                        viewCartUI();
                        break;
                    case 4:
                        cartService.clearCart(activeCustomer.getUserId());
                        System.out.println("Cart cleared successfully.");
                        break;
                    case 5:
                        checkoutUI();
                        break;
                    case 6:
                        viewOrdersUI();
                        break;
                    case 7:
                        cancelOrderUI();
                        break;
                    case 8:
                        rateOrderUI();
                        break;
                    case 9:
                        addAddressUI();
                        break;
                    case 10:
                        selectOrCreateCustomer();
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

    private void selectOrCreateCustomer() {
        System.out.println("\n--- Select / Register Customer ---");
        List<com.hotel.models.User> customers = userService.getUsersByRole(com.hotel.models.UserRole.CUSTOMER);
        if (!customers.isEmpty()) {
            System.out.println("Existing Customers:");
            for (com.hotel.models.User u : customers) {
                System.out.printf("  [%d] %s (%s, %s)%n", u.getUserId(), u.getName(), u.getPhone(), u.getEmail());
            }
            System.out.println("  [0] Register a New Customer");
            System.out.print("Select Customer ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (id > 0) {
                com.hotel.models.User u = userService.getUserById(id);
                if (u instanceof Customer) {
                    activeCustomer = (Customer) u;
                    System.out.println("Logged in as " + activeCustomer.getName());
                    return;
                }
            }
        }

        // Register new
        System.out.print("Enter Your Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        activeCustomer = userService.registerCustomer(name, phone, email);
        System.out.print("Enter Delivery Address Line: ");
        String addrLine = scanner.nextLine().trim();
        System.out.print("Enter City (e.g. Bangalore / Mumbai): ");
        String city = scanner.nextLine().trim();
        System.out.print("Enter Postal Code: ");
        String pin = scanner.nextLine().trim();

        userService.addCustomerAddress(activeCustomer.getUserId(), city, addrLine, pin);
        System.out.println("Customer registered successfully with ID: " + activeCustomer.getUserId());
    }

    private void browseRestaurants() {
        System.out.println("\n=== RESTAURANTS LIST ===");
        List<Restaurant> list = restaurantService.getAllRestaurants();
        if (list.isEmpty()) {
            System.out.println("No restaurants available.");
            return;
        }
        for (Restaurant r : list) {
            System.out.println(r);
            List<MenuItem> items = restaurantService.getMenuItemsByRestaurant(r.getRestaurantId());
            for (MenuItem item : items) {
                System.out.println("   --> " + item);
            }
        }
    }

    private void addItemToCartUI() {
        System.out.print("Enter Menu Item ID to add: ");
        int itemId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Quantity (1 - 10): ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        cartService.addItemToCart(activeCustomer.getUserId(), itemId, qty);
        MenuItem mi = restaurantService.getMenuItemById(itemId);
        System.out.printf("Added %d x '%s' to cart!%n", qty, mi != null ? mi.getName() : "Item");
    }

    private void viewCartUI() {
        Cart cart = cartService.getCart(activeCustomer.getUserId());
        System.out.println("\n=== YOUR CART ===");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        Restaurant r = restaurantService.getRestaurantById(cart.getRestaurantId());
        System.out.printf("Restaurant: %s (ID: %d)%n", (r != null ? r.getName() : "Unknown"), cart.getRestaurantId());
        double subtotal = 0.0;
        for (CartItem ci : cart.getItems()) {
            MenuItem mi = restaurantService.getMenuItemById(ci.getItemId());
            double price = mi != null ? mi.getPrice() : 0.0;
            double lineTotal = price * ci.getQuantity();
            subtotal += lineTotal;
            System.out.printf(" - %s x %d = Rs. %.2f (%s)%n",
                    (mi != null ? mi.getName() : "Item #" + ci.getItemId()),
                    ci.getQuantity(), lineTotal,
                    (mi != null && mi.isAvailable()) ? "Available" : "UNAVAILABLE");
        }
        System.out.printf("Cart Subtotal: Rs. %.2f%n", subtotal);
        if (r != null) {
            System.out.printf("Restaurant Min Order: Rs. %.2f %s%n",
                    r.getMinimumOrderValue(),
                    subtotal >= r.getMinimumOrderValue() ? "[Eligible]" : "[Add more items to meet minimum]");
        }
    }

    private void checkoutUI() {
        Cart cart = cartService.getCart(activeCustomer.getUserId());
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Add items before checking out.");
            return;
        }

        viewCartUI();

        // Select address
        List<Address> addresses = activeCustomer.getAddresses();
        if (addresses.isEmpty()) {
            System.out.println("You have no saved addresses. Please add an address first.");
            addAddressUI();
            addresses = activeCustomer.getAddresses();
        }

        System.out.println("\nSelect Delivery Address:");
        for (Address a : addresses) {
            System.out.printf(" [%d] %s%n", a.getAddressId(), a);
        }
        System.out.print("Enter Address ID: ");
        int addressId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Coupon Code (or press Enter to skip): ");
        String couponCode = scanner.nextLine().trim();

        System.out.println("Payment Methods:");
        System.out.println(" 1. UPI (e.g. john@okaxis)");
        System.out.println(" 2. Card (Debit/Credit Card)");
        System.out.println(" 3. COD (Cash On Delivery - max Rs. 1500)");
        System.out.print("Select Payment Method (1/2/3): ");
        int pChoice = Integer.parseInt(scanner.nextLine().trim());

        String method = "UPI";
        String details = "user@upi";
        if (pChoice == 1) {
            method = "UPI";
            System.out.print("Enter UPI ID (e.g. user@okhdfc): ");
            details = scanner.nextLine().trim();
        } else if (pChoice == 2) {
            method = "Card";
            System.out.print("Enter 16-digit Card Number: ");
            details = scanner.nextLine().trim();
        } else if (pChoice == 3) {
            method = "COD";
            details = "Cash on Delivery";
        }

        Order order = orderService.placeOrder(activeCustomer.getUserId(), addressId,
                couponCode.isEmpty() ? null : couponCode,
                method, details, restaurantService);

        System.out.println("\n==================================");
        System.out.printf("   ORDER #%d PLACED SUCCESSFULLY!%n", order.getOrderId());
        System.out.printf("   Items Total:      Rs. %.2f%n", order.getItemsTotal());
        System.out.printf("   Delivery Fee:     Rs. %.2f%n", order.getDeliveryFee());
        System.out.printf("   GST (5%%):         Rs. %.2f%n", order.getGstAmount());
        System.out.printf("   Coupon Discount: -Rs. %.2f%n", order.getCouponDiscount());
        System.out.printf("   FINAL TOTAL:      Rs. %.2f%n", order.getTotalAmount());
        System.out.printf("   Status:           %s%n", order.getStatus());
        System.out.println("==================================");
    }

    private void viewOrdersUI() {
        List<Order> orders = orderService.getOrdersByCustomer(activeCustomer.getUserId());
        System.out.println("\n=== YOUR ORDERS ===");
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        for (Order o : orders) {
            System.out.println(o);
            List<OrderStatusHistory> histories = orderService.getOrderRepository().getHistoryByOrderId(o.getOrderId());
            for (OrderStatusHistory h : histories) {
                System.out.println("     " + h);
            }
        }
    }

    private void cancelOrderUI() {
        System.out.print("Enter Order ID to cancel: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Cancellation Reason: ");
        String reason = scanner.nextLine().trim();

        orderService.cancelOrder(orderId, activeCustomer.getUserId(), reason);
        System.out.println("Order cancelled successfully.");
    }

    private void rateOrderUI() {
        System.out.print("Enter Delivered Order ID to rate: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Rate Restaurant (1 to 5): ");
        int rRating = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Rate Delivery Partner (1 to 5): ");
        int dRating = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Review Comment: ");
        String comment = scanner.nextLine().trim();

        reviewService.submitReview(orderId, activeCustomer.getUserId(), rRating, dRating, comment);
        System.out.println("Thank you for your rating!");
    }

    private void addAddressUI() {
        System.out.print("Enter Delivery Address Line: ");
        String addrLine = scanner.nextLine().trim();
        System.out.print("Enter City: ");
        String city = scanner.nextLine().trim();
        System.out.print("Enter Postal Code: ");
        String pin = scanner.nextLine().trim();

        Address addr = userService.addCustomerAddress(activeCustomer.getUserId(), city, addrLine, pin);
        System.out.println("Added address: " + addr);
    }
}
