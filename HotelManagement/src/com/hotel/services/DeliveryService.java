package com.hotel.services;

import com.hotel.models.Delivery;
import com.hotel.models.DeliveryPartner;
import com.hotel.models.DeliveryStatus;
import com.hotel.models.Order;
import com.hotel.models.OrderStatus;
import com.hotel.models.PartnerStatus;
import com.hotel.models.Payment;
import com.hotel.models.PaymentStatus;
import com.hotel.models.Restaurant;
import com.hotel.repo.DeliveryRepository;
import com.hotel.repo.OrderRepository;
import com.hotel.repo.PaymentRepository;
import com.hotel.repo.RestaurantRepository;
import com.hotel.repo.UserRepository;
import java.util.List;

public class DeliveryService {
    public static final double DELIVERY_COMMISSION = 30.0;
    public static final int MAX_DAILY_REJECTIONS = 3;

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private int nextDeliveryId = 1;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           UserRepository userRepository,
                           RestaurantRepository restaurantRepository,
                           OrderRepository orderRepository,
                           PaymentRepository paymentRepository) {
        this.deliveryRepository = deliveryRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public DeliveryRepository getDeliveryRepository() {
        return deliveryRepository;
    }

    /**
     * Module 4: When an order moves to Ready for Pickup, the system assigns
     * an Available delivery partner from the same city as the restaurant.
     * If no partner is available, order waits in a queue.
     */
    public Delivery assignDeliveryPartnerOrQueue(int orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId());
        String city = restaurant.getCity();

        DeliveryPartner availablePartner = findAvailablePartnerInCity(city);
        if (availablePartner != null) {
            return assignPartnerToOrder(order, availablePartner);
        } else {
            System.out.printf("[Delivery System] No available delivery partners in %s. Order #%d placed in waiting queue.%n",
                    city, orderId);
            deliveryRepository.enqueueOrder(city, orderId);
            return null;
        }
    }

    private Delivery assignPartnerToOrder(Order order, DeliveryPartner partner) {
        partner.setStatus(PartnerStatus.BUSY);
        Delivery delivery = new Delivery(nextDeliveryId++, order.getOrderId(), partner.getUserId());
        deliveryRepository.saveDelivery(delivery);
        System.out.printf("[Delivery System] Order #%d assigned to Delivery Partner '%s' (ID: %d) in %s.%n",
                order.getOrderId(), partner.getName(), partner.getUserId(), partner.getCity());
        return delivery;
    }

    public DeliveryPartner findAvailablePartnerInCity(String city) {
        List<DeliveryPartner> partners = userRepository.findPartnersByCity(city);
        for (DeliveryPartner dp : partners) {
            if (dp.getStatus() == PartnerStatus.AVAILABLE) {
                return dp;
            }
        }
        return null;
    }

    /**
     * Module 4: A partner can reject an assignment. System assigns next available partner.
     * A partner can reject at most 3 assignments per day.
     */
    public boolean rejectAssignment(int deliveryId, int partnerId) {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        if (delivery == null || delivery.getPartnerId() != partnerId) {
            throw new IllegalArgumentException("Invalid delivery or partner mismatch.");
        }

        DeliveryPartner partner = (DeliveryPartner) userRepository.findById(partnerId);
        if (partner.getDailyRejections() >= MAX_DAILY_REJECTIONS) {
            throw new IllegalStateException("Partner has already reached maximum daily rejections limit (" + MAX_DAILY_REJECTIONS + "). Cannot reject more assignments today.");
        }

        partner.incrementDailyRejections();
        partner.setStatus(PartnerStatus.AVAILABLE);
        delivery.setStatus(DeliveryStatus.REJECTED);

        System.out.printf("[Delivery System] Partner %s rejected Delivery #%d. Rejection count: %d/%d.%n",
                partner.getName(), deliveryId, partner.getDailyRejections(), MAX_DAILY_REJECTIONS);

        // Reassign the order to the next available partner
        assignDeliveryPartnerOrQueue(delivery.getOrderId());
        return true;
    }

    public void markOrderPickedUp(int deliveryId, int partnerId) {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        if (delivery == null || delivery.getPartnerId() != partnerId) {
            throw new IllegalArgumentException("Delivery not found or partner mismatch.");
        }
        delivery.setStatus(DeliveryStatus.PICKED_UP);

        Order order = orderRepository.findById(delivery.getOrderId());
        if (order != null) {
            orderRepository.recordStatusHistory(order.getOrderId(), order.getStatus(), OrderStatus.OUT_FOR_DELIVERY, "Picked up by partner");
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        }
        System.out.printf("[Delivery System] Order #%d is now OUT FOR DELIVERY by Partner #%d.%n", delivery.getOrderId(), partnerId);
    }

    /**
     * Module 4: Delivery completed. Partner becomes Available again.
     * Fixed earning of Rs. 30 added to partner's record.
     * If COD payment, mark it Paid.
     * Check if other orders in the queue are waiting for a partner.
     */
    public void markOrderDelivered(int deliveryId, int partnerId) {
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId);
        if (delivery == null || delivery.getPartnerId() != partnerId) {
            throw new IllegalArgumentException("Delivery not found or partner mismatch.");
        }

        delivery.setStatus(DeliveryStatus.DELIVERED);

        Order order = orderRepository.findById(delivery.getOrderId());
        if (order != null) {
            orderRepository.recordStatusHistory(order.getOrderId(), order.getStatus(), OrderStatus.DELIVERED, "Delivered to customer");
            order.setStatus(OrderStatus.DELIVERED);

            // COD payment becomes Paid
            Payment payment = paymentRepository.findPaymentByOrderId(order.getOrderId());
            if (payment != null && payment.getPaymentMethod().equalsIgnoreCase("COD") && payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.PAID);
                System.out.println("[Payment] COD Payment of Rs. " + payment.getAmount() + " collected and marked PAID.");
            }
        }

        // Add Rs. 30 earning to partner
        DeliveryPartner partner = (DeliveryPartner) userRepository.findById(partnerId);
        partner.addEarnings(DELIVERY_COMMISSION);
        deliveryRepository.recordEarnings(partnerId, deliveryId, DELIVERY_COMMISSION);
        partner.setStatus(PartnerStatus.AVAILABLE);

        System.out.printf("[Delivery System] Order #%d successfully DELIVERED! Partner %s earned Rs.%.2f. Total earnings: Rs.%.2f.%n",
                delivery.getOrderId(), partner.getName(), DELIVERY_COMMISSION, partner.getTotalEarnings());

        // Check if there is an order in the city's queue waiting for an available partner
        Restaurant r = restaurantRepository.findById(order.getRestaurantId());
        Integer queuedOrderId = deliveryRepository.dequeueOrder(r.getCity());
        if (queuedOrderId != null) {
            Order nextOrder = orderRepository.findById(queuedOrderId);
            if (nextOrder != null && nextOrder.getStatus() == OrderStatus.READY_FOR_PICKUP) {
                System.out.printf("[Delivery System] Partner %s immediately assigned waiting order #%d from queue!%n",
                        partner.getName(), queuedOrderId);
                assignPartnerToOrder(nextOrder, partner);
            }
        }
    }

    public List<Delivery> getDeliveriesByPartner(int partnerId) {
        return deliveryRepository.findDeliveriesByPartnerId(partnerId);
    }

    public Delivery getDeliveryByOrderId(int orderId) {
        return deliveryRepository.findDeliveryByOrderId(orderId);
    }
}
