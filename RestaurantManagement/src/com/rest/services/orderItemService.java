package com.rest.services;

import com.rest.models.orderItem;
import com.rest.repository.orderItemRepository;
import java.util.List;

public class orderItemService {
    private final orderItemRepository repository;

    public orderItemService() {
        this.repository = new orderItemRepository();
    }

    public orderItemService(orderItemRepository repository) {
        this.repository = repository;
    }

    public void addOrderItem(orderItem item) {
        repository.save(item);
    }

    public List<orderItem> getItemsByOrderId(int orderId) {
        return repository.findByOrderId(orderId);
    }

    public List<orderItem> getAllOrderItems() {
        return repository.findAll();
    }

    public orderItem getOrderItemById(int id) {
        return repository.findById(id);
    }
}
