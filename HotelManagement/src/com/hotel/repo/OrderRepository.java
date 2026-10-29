package com.hotel.repo;

import com.hotel.models.Order;
import com.hotel.models.OrderStatus;
import com.hotel.models.OrderStatusHistory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {
    private final Map<Integer, Order> orders = new HashMap<>();
    private final List<OrderStatusHistory> statusHistories = new ArrayList<>();
    private int nextHistoryId = 1;

    public void save(Order order) {
        orders.put(order.getOrderId(), order);
    }

    public Order findById(int orderId) {
        return orders.get(orderId);
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> findByCustomerId(int customerId) {
        List<Order> list = new ArrayList<>();
        for (Order o : orders.values()) {
            if (o.getCustomerId() == customerId) {
                list.add(o);
            }
        }
        return list;
    }

    public List<Order> findByRestaurantId(int restaurantId) {
        List<Order> list = new ArrayList<>();
        for (Order o : orders.values()) {
            if (o.getRestaurantId() == restaurantId) {
                list.add(o);
            }
        }
        return list;
    }

    public int countActiveOrdersByCustomer(int customerId) {
        int count = 0;
        for (Order o : orders.values()) {
            if (o.getCustomerId() == customerId && o.isActive()) {
                count++;
            }
        }
        return count;
    }

    public int countActiveOrdersByRestaurant(int restaurantId) {
        int count = 0;
        for (Order o : orders.values()) {
            if (o.getRestaurantId() == restaurantId && o.isActive()) {
                count++;
            }
        }
        return count;
    }

    public void recordStatusHistory(int orderId, OrderStatus oldStatus, OrderStatus newStatus, String reason) {
        OrderStatusHistory history = new OrderStatusHistory(nextHistoryId++, orderId, oldStatus, newStatus, reason);
        statusHistories.add(history);
    }

    public List<OrderStatusHistory> getHistoryByOrderId(int orderId) {
        List<OrderStatusHistory> list = new ArrayList<>();
        for (OrderStatusHistory h : statusHistories) {
            if (h.getOrderId() == orderId) {
                list.add(h);
            }
        }
        return list;
    }
}
