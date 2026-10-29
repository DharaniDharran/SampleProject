package com.hotel.repo;

import com.hotel.models.Delivery;
import com.hotel.models.DeliveryEarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class DeliveryRepository {
    private final Map<Integer, Delivery> deliveries = new HashMap<>();
    private final List<DeliveryEarnings> earningsList = new ArrayList<>();
    // Per-city delivery queue (order IDs waiting for available partner)
    private final Map<String, Queue<Integer>> cityQueues = new HashMap<>();
    private int nextEarningId = 1;

    public void saveDelivery(Delivery delivery) {
        deliveries.put(delivery.getDeliveryId(), delivery);
    }

    public Delivery findDeliveryById(int deliveryId) {
        return deliveries.get(deliveryId);
    }

    public Delivery findDeliveryByOrderId(int orderId) {
        for (Delivery d : deliveries.values()) {
            if (d.getOrderId() == orderId) {
                return d;
            }
        }
        return null;
    }

    public List<Delivery> findAllDeliveries() {
        return new ArrayList<>(deliveries.values());
    }

    public List<Delivery> findDeliveriesByPartnerId(int partnerId) {
        List<Delivery> list = new ArrayList<>();
        for (Delivery d : deliveries.values()) {
            if (d.getPartnerId() == partnerId) {
                list.add(d);
            }
        }
        return list;
    }

    public void enqueueOrder(String city, int orderId) {
        cityQueues.computeIfAbsent(city.toLowerCase(), k -> new LinkedList<>()).offer(orderId);
    }

    public Integer dequeueOrder(String city) {
        Queue<Integer> queue = cityQueues.get(city.toLowerCase());
        if (queue != null && !queue.isEmpty()) {
            return queue.poll();
        }
        return null;
    }

    public Queue<Integer> getQueueForCity(String city) {
        return cityQueues.computeIfAbsent(city.toLowerCase(), k -> new LinkedList<>());
    }

    public void recordEarnings(int partnerId, int deliveryId, double amount) {
        DeliveryEarnings earnings = new DeliveryEarnings(nextEarningId++, partnerId, deliveryId, amount);
        earningsList.add(earnings);
    }

    public List<DeliveryEarnings> getEarningsByPartnerId(int partnerId) {
        List<DeliveryEarnings> list = new ArrayList<>();
        for (DeliveryEarnings de : earningsList) {
            if (de.getPartnerId() == partnerId) {
                list.add(de);
            }
        }
        return list;
    }

    public List<DeliveryEarnings> getAllEarnings() {
        return new ArrayList<>(earningsList);
    }
}
