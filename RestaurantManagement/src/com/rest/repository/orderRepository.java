package com.rest.repository;

import com.rest.models.order;
import java.util.ArrayList;
import java.util.List;

public class orderRepository {
    private final List<order> orders = new ArrayList<>();

    public void save(order ord) {
        orders.add(ord);
    }

    public List<order> findAll() {
        return orders;
    }

    public order findById(int id) {
        for (order ord : orders) {
            if (ord.getOrder_id() == id) {
                return ord;
            }
        }
        return null;
    }

    public List<order> findByTableId(int tableId) {
        List<order> result = new ArrayList<>();
        for (order ord : orders) {
            if (ord.getTable_id() == tableId) {
                result.add(ord);
            }
        }
        return result;
    }
}
