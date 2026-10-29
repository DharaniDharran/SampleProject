package com.rest.repository;

import com.rest.models.orderItem;
import java.util.ArrayList;
import java.util.List;

public class orderItemRepository {
    private final List<orderItem> orderItems = new ArrayList<>();

    public void save(orderItem item) {
        orderItems.add(item);
    }

    public List<orderItem> findAll() {
        return orderItems;
    }

    public List<orderItem> findByOrderId(int orderId) {
        List<orderItem> result = new ArrayList<>();
        for (orderItem item : orderItems) {
            if (item.getOrder_id() == orderId) {
                result.add(item);
            }
        }
        return result;
    }

    public orderItem findById(int id) {
        for (orderItem item : orderItems) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}
