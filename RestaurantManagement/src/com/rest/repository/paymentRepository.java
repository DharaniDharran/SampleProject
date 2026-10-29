package com.rest.repository;

import com.rest.models.payment;
import java.util.ArrayList;
import java.util.List;

public class paymentRepository {
    private final List<payment> payments = new ArrayList<>();

    public void save(payment p) {
        payments.add(p);
    }

    public List<payment> findAll() {
        return payments;
    }

    public payment findById(int id) {
        for (payment p : payments) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<payment> findByOrderId(int orderId) {
        List<payment> result = new ArrayList<>();
        for (payment p : payments) {
            if (p.getOrderId() == orderId) {
                result.add(p);
            }
        }
        return result;
    }
}
