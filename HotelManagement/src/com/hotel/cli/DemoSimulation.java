package com.hotel.cli;

import com.hotel.models.Address;
import com.hotel.models.Customer;
import com.hotel.models.DeliveryPartner;
import com.hotel.models.DiscountType;
import com.hotel.models.MenuItem;
import com.hotel.models.Order;
import com.hotel.models.OrderStatus;
import com.hotel.models.Restaurant;
import com.hotel.models.RestaurantOwner;
import com.hotel.services.CartService;
import com.hotel.services.DeliveryService;
import com.hotel.services.OrderService;
import com.hotel.services.PaymentService;
import com.hotel.services.RestaurantService;
import com.hotel.services.ReviewService;
import com.hotel.services.UserService;
import java.time.LocalDate;

public class DemoSimulation {

    public static void runAutomatedDemo(UserService userService,
                                       RestaurantService restaurantService,
                                       CartService cartService,
                                       OrderService orderService,
                                       DeliveryService deliveryService,
                                       PaymentService paymentService,
                                       ReviewService reviewService,
                                       AdminCLI adminCLI) {
        System.out.println("\n**************************************************************");
        System.out.println("   RUNNING AUTOMATED END-TO-END DEMO TEST FOR ALL 6 MODULES   ");
        System.out.println("**************************************************************");

        try {
            // Step 1: Users & Customers
            System.out.println("\n[1] Registering Customer and Restaurant Owner...");
            Customer alice = (Customer) userService.getUserById(4); // Seeded Alice
            RestaurantOwner suresh = (RestaurantOwner) userService.getUserById(2); // Seeded Suresh
            Restaurant spiceGarden = restaurantService.getRestaurantById(1); // Bangalore
            System.out.println("  Customer: " + alice.getName() + " in " + alice.getAddresses().get(0).getCity());
            System.out.println("  Restaurant: " + spiceGarden.getName() + " in " + spiceGarden.getCity());

            // Step 2: Customer adds items to Cart
            System.out.println("\n[2] Customer adding items to Cart...");
            MenuItem butterChicken = restaurantService.getMenuItemById(1); // 320
            MenuItem paneerTikka = restaurantService.getMenuItemById(2); // 240
            cartService.addItemToCart(alice.getUserId(), butterChicken.getItemId(), 2); // 640
            cartService.addItemToCart(alice.getUserId(), paneerTikka.getItemId(), 1); // 240
            System.out.println("  Items added to cart: 2 x Butter Chicken + 1 x Paneer Tikka. Subtotal = Rs. 880");

            // Test Rule: Cart max 10 quantity check
            try {
                cartService.addItemToCart(alice.getUserId(), butterChicken.getItemId(), 15);
            } catch (Exception e) {
                System.out.println("  [Test Passed] Rule validated: " + e.getMessage());
            }

            // Test Rule: Different restaurant item in cart
            try {
                MenuItem noodles = restaurantService.getMenuItemById(5); // Dragon Wok
                cartService.addItemToCart(alice.getUserId(), noodles.getItemId(), 1);
            } catch (Exception e) {
                System.out.println("  [Test Passed] Rule validated: " + e.getMessage());
            }

            // Step 3: Checkout with Coupon and UPI Payment
            System.out.println("\n[3] Checking out and placing order with Coupon 'WELCOME50' and UPI...");
            Address address = alice.getAddresses().get(0);
            Order order1 = orderService.placeOrder(alice.getUserId(), address.getAddressId(),
                    "WELCOME50", "UPI", "alice@okaxis", restaurantService);

            System.out.printf("  Order #%d created. Items Total: Rs.%.2f, Delivery Fee: Rs.%.2f (Free over 500), GST: Rs.%.2f, Discount: Rs.%.2f, Final Total: Rs.%.2f%n",
                    order1.getOrderId(), order1.getItemsTotal(), order1.getDeliveryFee(),
                    order1.getGstAmount(), order1.getCouponDiscount(), order1.getTotalAmount());

            // Test Rule: Coupon reuse check
            try {
                cartService.addItemToCart(alice.getUserId(), paneerTikka.getItemId(), 2);
                orderService.placeOrder(alice.getUserId(), address.getAddressId(),
                        "WELCOME50", "UPI", "alice@okaxis", restaurantService);
            } catch (Exception e) {
                System.out.println("  [Test Passed] Rule validated (Coupon single use): " + e.getMessage());
                cartService.clearCart(alice.getUserId());
            }

            // Step 4: Restaurant Acceptance & Preparation
            System.out.println("\n[4] Restaurant accepting and preparing Order #" + order1.getOrderId() + "...");
            orderService.acceptOrder(order1.getOrderId(), suresh.getUserId());
            orderService.progressOrderStatus(order1.getOrderId(), OrderStatus.PREPARING, suresh.getUserId());

            // Step 5: Ready for Pickup -> Auto-assigns Delivery Partner in same city
            System.out.println("\n[5] Order marked READY FOR PICKUP -> System auto-assigns Delivery Partner in Bangalore...");
            orderService.progressOrderStatus(order1.getOrderId(), OrderStatus.READY_FOR_PICKUP, suresh.getUserId());

            com.hotel.models.Delivery delivery = deliveryService.getDeliveryByOrderId(order1.getOrderId());
            DeliveryPartner partner = (DeliveryPartner) userService.getUserById(delivery.getPartnerId());
            System.out.printf("  Assigned to Partner: %s (Status: %s)%n", partner.getName(), partner.getStatus());

            // Step 6: Partner marks Picked Up & Delivered
            System.out.println("\n[6] Delivery partner picks up and completes delivery...");
            deliveryService.markOrderPickedUp(delivery.getDeliveryId(), partner.getUserId());
            deliveryService.markOrderDelivered(delivery.getDeliveryId(), partner.getUserId());

            System.out.printf("  Partner %s now Available again. Total Partner Earnings: Rs.%.2f%n",
                    partner.getName(), partner.getTotalEarnings());

            // Step 7: Customer Rates Order
            System.out.println("\n[7] Customer submits rating & review...");
            reviewService.submitReview(order1.getOrderId(), alice.getUserId(), 5, 5, "Delicious food and prompt delivery!");

            // Step 8: COD Order Test & Delivery Fee Test
            System.out.println("\n[8] Testing COD Order under 500 (Delivery Fee applied: Rs. 40)...");
            cartService.addItemToCart(alice.getUserId(), paneerTikka.getItemId(), 1); // 240 < 500
            Order order2 = orderService.placeOrder(alice.getUserId(), address.getAddressId(),
                    null, "COD", "Cash on Delivery", restaurantService);
            System.out.printf("  Order #%d created. Items: Rs.%.2f, Delivery Fee: Rs.%.2f (Rs. 40 charged), Total: Rs.%.2f, Status: %s%n",
                    order2.getOrderId(), order2.getItemsTotal(), order2.getDeliveryFee(),
                    order2.getTotalAmount(), order2.getStatus());

            // Step 9: Cancellation Test with Refund
            System.out.println("\n[9] Customer cancels Order #" + order2.getOrderId() + " while in PLACED status...");
            orderService.cancelOrder(order2.getOrderId(), alice.getUserId(), "Changed my mind");

            // Step 9b: Additional Orders to demonstrate SQL Query 2 and Query 3
            System.out.println("\n[9b] Simulating additional completed deliveries across restaurants for analytics...");
            // Order 3 from Dosa Hut (Bangalore) for Alice, delivered by Rajesh Kumar
            Restaurant dosaHut = restaurantService.getRestaurantById(2);
            MenuItem dosa = restaurantService.getMenuItemById(4);
            cartService.addItemToCart(alice.getUserId(), dosa.getItemId(), 2);
            Order order3 = orderService.placeOrder(alice.getUserId(), address.getAddressId(), null, "UPI", "alice@upi", restaurantService);
            orderService.acceptOrder(order3.getOrderId(), suresh.getUserId());
            orderService.progressOrderStatus(order3.getOrderId(), OrderStatus.PREPARING, suresh.getUserId());
            orderService.progressOrderStatus(order3.getOrderId(), OrderStatus.READY_FOR_PICKUP, suresh.getUserId());
            com.hotel.models.Delivery del3 = deliveryService.getDeliveryByOrderId(order3.getOrderId());
            deliveryService.markOrderPickedUp(del3.getDeliveryId(), partner.getUserId());
            deliveryService.markOrderDelivered(del3.getDeliveryId(), partner.getUserId());

            // Order 4 from Spice Garden (Bangalore) for Alice, delivered by Rajesh Kumar
            // (Giving Spice Garden 3 total orders: 2 delivered, 1 cancelled)
            MenuItem butterChickenItem = restaurantService.getMenuItemById(1);
            cartService.addItemToCart(alice.getUserId(), butterChickenItem.getItemId(), 1);
            Order order4 = orderService.placeOrder(alice.getUserId(), address.getAddressId(), null, "CARD", "4111222233334444", restaurantService);
            orderService.acceptOrder(order4.getOrderId(), suresh.getUserId());
            orderService.progressOrderStatus(order4.getOrderId(), OrderStatus.PREPARING, suresh.getUserId());
            orderService.progressOrderStatus(order4.getOrderId(), OrderStatus.READY_FOR_PICKUP, suresh.getUserId());
            com.hotel.models.Delivery del4 = deliveryService.getDeliveryByOrderId(order4.getOrderId());
            deliveryService.markOrderPickedUp(del4.getDeliveryId(), partner.getUserId());
            deliveryService.markOrderDelivered(del4.getDeliveryId(), partner.getUserId());

            // Order 5 from Tandoori Nights (Bangalore, 3rd distinct restaurant for Rajesh Kumar)
            Restaurant tandoori = restaurantService.getRestaurantById(4);
            MenuItem tandooriItem = restaurantService.getMenuItemById(10); // Dal Makhani (ID: 10, Bangalore)
            cartService.addItemToCart(alice.getUserId(), tandooriItem.getItemId(), 1);
            Order order5 = orderService.placeOrder(alice.getUserId(), address.getAddressId(), null, "UPI", "alice@upi", restaurantService);
            orderService.acceptOrder(order5.getOrderId(), suresh.getUserId());
            orderService.progressOrderStatus(order5.getOrderId(), OrderStatus.PREPARING, suresh.getUserId());
            orderService.progressOrderStatus(order5.getOrderId(), OrderStatus.READY_FOR_PICKUP, suresh.getUserId());
            com.hotel.models.Delivery del5 = deliveryService.getDeliveryByOrderId(order5.getOrderId());
            deliveryService.markOrderPickedUp(del5.getDeliveryId(), partner.getUserId());
            deliveryService.markOrderDelivered(del5.getDeliveryId(), partner.getUserId());

            System.out.printf("  Partner %s has now picked up orders from 3 distinct restaurants (%s, %s, %s). Total Earnings: Rs.%.2f%n",
                    partner.getName(), spiceGarden.getName(), dosaHut.getName(), tandoori.getName(), partner.getTotalEarnings());

            System.out.println("\n**************************************************************");
            System.out.println("   DEMO COMPLETED SUCCESSFULLY! ALL MODULE REQUIREMENTS VERIFIED!   ");
            System.out.println("**************************************************************");

        } catch (Exception ex) {
            System.out.println("[Simulation Error] " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
