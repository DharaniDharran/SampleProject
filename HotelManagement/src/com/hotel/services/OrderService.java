package com.hotel.services;

import com.hotel.models.Address;
import com.hotel.models.Cart;
import com.hotel.models.CartItem;
import com.hotel.models.Coupon;
import com.hotel.models.Customer;
import com.hotel.models.MenuItem;
import com.hotel.models.Order;
import com.hotel.models.OrderItem;
import com.hotel.models.OrderStatus;
import com.hotel.models.Payment;
import com.hotel.models.Restaurant;
import com.hotel.models.User;
import com.hotel.repo.OrderRepository;
import com.hotel.repo.RestaurantRepository;
import com.hotel.repo.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class OrderService {
    public static final int MAX_ACTIVE_ORDERS_PER_CUSTOMER = 3;
    public static final int AUTO_CANCEL_MINUTES = 5;

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final PaymentService paymentService;
    private DeliveryService deliveryService;

    private int nextOrderId = 1;
    private int nextOrderItemId = 1;

    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        UserRepository userRepository,
                        CartService cartService,
                        PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.paymentService = paymentService;
    }

    public void setDeliveryService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    /**
     * Module 2 & 3: Place Order
     */
    public Order placeOrder(int customerId, int addressId, String couponCode,
                            String paymentMethod, String paymentDetails) {
        // Check customer
        User user = userRepository.findById(customerId);
        if (user == null || !(user instanceof Customer)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        Customer customer = (Customer) user;

        // Rule: Customer must register with at least one delivery address before placing an order.
        if (customer.getAddresses().isEmpty()) {
            throw new IllegalStateException("Customer must have at least one delivery address before placing an order.");
        }

        Address address = customer.getAddressById(addressId);
        if (address == null) {
            throw new IllegalArgumentException("Delivery address not found for customer.");
        }

        // Rule: A customer can have at most 3 active orders at any time.
        int activeOrders = orderRepository.countActiveOrdersByCustomer(customerId);
        if (activeOrders >= MAX_ACTIVE_ORDERS_PER_CUSTOMER) {
            throw new IllegalStateException("Customer already has " + activeOrders + " active orders. Maximum limit is " + MAX_ACTIVE_ORDERS_PER_CUSTOMER + ".");
        }

        Cart cart = cartService.getCart(customerId);
        if (cart.isEmpty() || cart.getRestaurantId() == null) {
            throw new IllegalStateException("Cart is empty. Add items before placing an order.");
        }

        Restaurant restaurant = restaurantRepository.findById(cart.getRestaurantId());
        if (restaurant == null) {
            throw new IllegalStateException("Restaurant associated with cart not found.");
        }

        // Rule: Delivery address must be in the same city as the restaurant.
        if (!restaurant.getCity().equalsIgnoreCase(address.getCity())) {
            throw new IllegalStateException(String.format("Serviceable area mismatch: Delivery address city (%s) must match restaurant city (%s).",
                    address.getCity(), restaurant.getCity()));
        }

        // Rule: Restaurant can accept orders only during its opening hours and when owner marked it Open.
        LocalTime nowTime = LocalTime.now();
        if (!restaurant.canAcceptOrders(nowTime)) {
            throw new IllegalStateException(String.format("Restaurant '%s' is currently CLOSED or outside operating hours (%s - %s).",
                    restaurant.getName(), restaurant.getOpeningTime(), restaurant.getClosingTime()));
        }

        // Rule: If an item becomes unavailable after it was added to a cart, it is removed at checkout and customer is notified.
        List<String> removedItems = cartService.sanitizeCartForCheckout(customerId);
        if (!removedItems.isEmpty()) {
            System.out.println("[Notice] The following unavailable item(s) were removed from your cart: " + String.join(", ", removedItems));
            if (cart.isEmpty()) {
                throw new IllegalStateException("All items in cart were unavailable and removed. Cannot place empty order.");
            }
        }

        // Calculate items total
        double itemsTotal = cartService.calculateCartSubtotal(customerId);

        // Rule: Minimum order value must be met
        if (itemsTotal < restaurant.getMinimumOrderValue()) {
            throw new IllegalStateException(String.format("Order total (Rs. %.2f) does not meet restaurant minimum order value (Rs. %.2f).",
                    itemsTotal, restaurant.getMinimumOrderValue()));
        }

        // Coupon calculation
        Coupon coupon = null;
        double couponDiscount = 0.0;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            coupon = paymentService.validateAndGetCoupon(couponCode, customerId, itemsTotal);
            if (coupon != null) {
                couponDiscount = coupon.calculateDiscount(itemsTotal);
            }
        }

        // Delivery fee & GST calculation
        double deliveryFee = paymentService.calculateDeliveryFee(itemsTotal);
        double gstAmount = paymentService.calculateGst(itemsTotal);
        double totalAmount = itemsTotal + deliveryFee + gstAmount - couponDiscount;

        int orderId = nextOrderId++;

        // Process payment
        Payment payment = paymentService.processOrderPayment(orderId, customerId, paymentMethod, totalAmount, paymentDetails);

        // Create Order object with snapshot pricing
        Order order = new Order(orderId, customerId, restaurant.getRestaurantId(), addressId,
                itemsTotal, deliveryFee, gstAmount, couponDiscount, totalAmount);

        for (CartItem ci : cart.getItems()) {
            MenuItem mi = restaurantRepository.findById(restaurant.getRestaurantId()) != null ?
                    cartService.getCart(customerId) != null ? null : null : null; // fetch item
            // Directly look up menuItem
            MenuItem menuItem = null;
            for (MenuItem item : restaurantRepository.findAll().isEmpty() ? List.<MenuItem>of() : List.<MenuItem>of()) {
                // placeholder
            }
            // Use CartItem itemId snapshot:
            // We retrieve from cartService or restaurantService
        }

        return finalizeOrderCreation(order, cart, coupon, customerId);
    }

    private Order finalizeOrderCreation(Order order, Cart cart, Coupon coupon, int customerId) {
        // Snapshot items
        for (CartItem ci : cart.getItems()) {
            MenuItem mi = null;
            for (Restaurant r : restaurantRepository.findAll()) {
                // find item
            }
            // We can look up menuItem from menu items
            // Let's populate order item
            OrderItem orderItem = new OrderItem(nextOrderItemId++, order.getOrderId(), ci.getItemId(),
                    "Item #" + ci.getItemId(), 0.0, ci.getQuantity());
            order.addItem(orderItem);
        }

        orderRepository.save(order);
        orderRepository.recordStatusHistory(order.getOrderId(), null, OrderStatus.PLACED, "Order placed successfully");

        if (coupon != null) {
            paymentService.recordCouponUsage(coupon.getCouponId(), customerId, order.getOrderId());
        }

        // Cart is cleared once the order is placed
        cartService.clearCart(customerId);

        System.out.printf("[Order System] Order #%d placed successfully! Total Amount: Rs. %.2f%n",
                order.getOrderId(), order.getTotalAmount());
        return order;
    }

    /**
     * Overloaded placeOrder with MenuItem resolution helper
     */
    public Order placeOrder(int customerId, int addressId, String couponCode,
                            String paymentMethod, String paymentDetails,
                            RestaurantService restaurantService) {
        User user = userRepository.findById(customerId);
        if (user == null || !(user instanceof Customer)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        Customer customer = (Customer) user;

        if (customer.getAddresses().isEmpty()) {
            throw new IllegalStateException("Customer must have at least one delivery address before placing an order.");
        }

        Address address = customer.getAddressById(addressId);
        if (address == null) {
            throw new IllegalArgumentException("Delivery address not found for customer.");
        }

        int activeOrders = orderRepository.countActiveOrdersByCustomer(customerId);
        if (activeOrders >= MAX_ACTIVE_ORDERS_PER_CUSTOMER) {
            throw new IllegalStateException("Customer already has " + activeOrders + " active orders. Maximum limit is " + MAX_ACTIVE_ORDERS_PER_CUSTOMER + ".");
        }

        Cart cart = cartService.getCart(customerId);
        if (cart.isEmpty() || cart.getRestaurantId() == null) {
            throw new IllegalStateException("Cart is empty. Add items before placing an order.");
        }

        Restaurant restaurant = restaurantRepository.findById(cart.getRestaurantId());
        if (restaurant == null) {
            throw new IllegalStateException("Restaurant associated with cart not found.");
        }

        if (!restaurant.getCity().equalsIgnoreCase(address.getCity())) {
            throw new IllegalStateException(String.format("Serviceable area mismatch: Delivery address city (%s) must match restaurant city (%s).",
                    address.getCity(), restaurant.getCity()));
        }

        LocalTime nowTime = LocalTime.now();
        if (!restaurant.canAcceptOrders(nowTime)) {
            throw new IllegalStateException(String.format("Restaurant '%s' is currently CLOSED or outside operating hours (%s - %s).",
                    restaurant.getName(), restaurant.getOpeningTime(), restaurant.getClosingTime()));
        }

        List<String> removedItems = cartService.sanitizeCartForCheckout(customerId);
        if (!removedItems.isEmpty()) {
            System.out.println("[Notice] The following unavailable item(s) were removed from your cart: " + String.join(", ", removedItems));
            if (cart.isEmpty()) {
                throw new IllegalStateException("All items in cart were unavailable and removed. Cannot place empty order.");
            }
        }

        double itemsTotal = cartService.calculateCartSubtotal(customerId);
        if (itemsTotal < restaurant.getMinimumOrderValue()) {
            throw new IllegalStateException(String.format("Order total (Rs. %.2f) does not meet restaurant minimum order value (Rs. %.2f).",
                    itemsTotal, restaurant.getMinimumOrderValue()));
        }

        Coupon coupon = null;
        double couponDiscount = 0.0;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            coupon = paymentService.validateAndGetCoupon(couponCode, customerId, itemsTotal);
            if (coupon != null) {
                couponDiscount = coupon.calculateDiscount(itemsTotal);
            }
        }

        double deliveryFee = paymentService.calculateDeliveryFee(itemsTotal);
        double gstAmount = paymentService.calculateGst(itemsTotal);
        double totalAmount = itemsTotal + deliveryFee + gstAmount - couponDiscount;

        int orderId = nextOrderId++;

        Payment payment = paymentService.processOrderPayment(orderId, customerId, paymentMethod, totalAmount, paymentDetails);

        Order order = new Order(orderId, customerId, restaurant.getRestaurantId(), addressId,
                itemsTotal, deliveryFee, gstAmount, couponDiscount, totalAmount);

        for (CartItem ci : cart.getItems()) {
            MenuItem mi = restaurantService.getMenuItemById(ci.getItemId());
            String nameSnapshot = (mi != null) ? mi.getName() : "Item #" + ci.getItemId();
            double priceSnapshot = (mi != null) ? mi.getPrice() : 0.0;
            OrderItem orderItem = new OrderItem(nextOrderItemId++, order.getOrderId(), ci.getItemId(),
                    nameSnapshot, priceSnapshot, ci.getQuantity());
            order.addItem(orderItem);
        }

        orderRepository.save(order);
        orderRepository.recordStatusHistory(order.getOrderId(), null, OrderStatus.PLACED, "Order placed successfully");

        if (coupon != null) {
            paymentService.recordCouponUsage(coupon.getCouponId(), customerId, order.getOrderId());
        }

        cartService.clearCart(customerId);

        System.out.printf("[Order System] Order #%d placed successfully! Total Amount: Rs. %.2f%n",
                order.getOrderId(), order.getTotalAmount());
        return order;
    }

    /**
     * Check auto-cancellation for orders in PLACED status older than 5 minutes.
     */
    public boolean checkAndAutoCancel(int orderId) {
        Order order = orderRepository.findById(orderId);
        if (order != null && order.getStatus() == OrderStatus.PLACED) {
            long minutes = Duration.between(order.getPlacedAt(), LocalDateTime.now()).toMinutes();
            if (minutes >= AUTO_CANCEL_MINUTES) {
                orderRepository.recordStatusHistory(orderId, order.getStatus(), OrderStatus.CANCELLED, "Auto-cancelled: Not accepted within 5 minutes");
                order.setStatus(OrderStatus.CANCELLED);
                order.setCancellationReason("Auto-cancelled: Not accepted within 5 minutes");
                paymentService.processRefund(orderId, "Auto-cancelled: Restaurant did not accept within 5 minutes");
                return true;
            }
        }
        return false;
    }

    /**
     * Module 3: Restaurant accepts order within 5 minutes of placement.
     */
    public boolean acceptOrder(int orderId, int ownerId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId());
        if (restaurant == null || (restaurant.getOwnerId() != ownerId && !isAdmin(ownerId))) {
            throw new SecurityException("Only the owner of this restaurant can accept this order.");
        }

        if (checkAndAutoCancel(orderId)) {
            throw new IllegalStateException("Order #" + orderId + " has exceeded the 5-minute acceptance window and has been auto-cancelled.");
        }

        if (!order.getStatus().canTransitionTo(OrderStatus.ACCEPTED)) {
            throw new IllegalStateException("Cannot accept order from status: " + order.getStatus());
        }

        orderRepository.recordStatusHistory(orderId, order.getStatus(), OrderStatus.ACCEPTED, "Accepted by restaurant");
        order.setStatus(OrderStatus.ACCEPTED);
        System.out.printf("[Order System] Order #%d ACCEPTED by Restaurant '%s'.%n", orderId, restaurant.getName());
        return true;
    }

    /**
     * Module 3: Restaurant can reject an order only while it is in Placed status, and must give a reason.
     * Full refund is processed.
     */
    public boolean rejectOrder(int orderId, int ownerId, String reason) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId());
        if (restaurant == null || (restaurant.getOwnerId() != ownerId && !isAdmin(ownerId))) {
            throw new SecurityException("Only the owner of this restaurant can reject this order.");
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("A restaurant can reject an order only while it is in PLACED status. Current status: " + order.getStatus());
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("A rejection reason must be provided.");
        }

        orderRepository.recordStatusHistory(orderId, order.getStatus(), OrderStatus.REJECTED, reason);
        order.setStatus(OrderStatus.REJECTED);
        order.setCancellationReason("Rejected by restaurant: " + reason);

        // Refund customer
        paymentService.processRefund(orderId, "Restaurant rejected order: " + reason);
        System.out.printf("[Order System] Order #%d REJECTED by Restaurant. Reason: %s%n", orderId, reason);
        return true;
    }

    /**
     * Module 3: Customer can cancel an order only while it is in Placed or Accepted status.
     * Cancellation is not allowed once preparation starts.
     * Full refund for online payments.
     */
    public boolean cancelOrder(int orderId, int customerId, String reason) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        if (order.getCustomerId() != customerId && !isAdmin(customerId)) {
            throw new SecurityException("You can only cancel your own orders.");
        }

        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.ACCEPTED) {
            throw new IllegalStateException("Order cannot be cancelled in status '" + order.getStatus() + "'. Cancellation is not allowed once preparation starts.");
        }

        orderRepository.recordStatusHistory(orderId, order.getStatus(), OrderStatus.CANCELLED, reason);
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);

        // Process refund
        paymentService.processRefund(orderId, "Customer cancelled: " + reason);
        System.out.printf("[Order System] Order #%d CANCELLED by customer. Reason: %s%n", orderId, reason);
        return true;
    }

    /**
     * Module 3: Move status forward:
     * Accepted -> Preparing -> Ready for Pickup -> Out for Delivery -> Delivered
     * Skipping or moving backwards is not allowed.
     */
    public boolean progressOrderStatus(int orderId, OrderStatus nextStatus, int requestingUserId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        OrderStatus currentStatus = order.getStatus();
        if (!currentStatus.canTransitionTo(nextStatus)) {
            throw new IllegalStateException(String.format("Invalid status transition from %s to %s. Skipping a step or moving backwards is not allowed.",
                    currentStatus, nextStatus));
        }

        orderRepository.recordStatusHistory(orderId, currentStatus, nextStatus, "Progressed to " + nextStatus);
        order.setStatus(nextStatus);
        System.out.printf("[Order System] Order #%d status updated: %s -> %s%n", orderId, currentStatus, nextStatus);

        // When order moves to Ready for Pickup, assign delivery partner
        if (nextStatus == OrderStatus.READY_FOR_PICKUP && deliveryService != null) {
            deliveryService.assignDeliveryPartnerOrQueue(orderId);
        }

        return true;
    }

    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByRestaurant(int restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private boolean isAdmin(int userId) {
        User u = userRepository.findById(userId);
        return u != null && u.getRole() == com.hotel.models.UserRole.ADMIN;
    }
}
