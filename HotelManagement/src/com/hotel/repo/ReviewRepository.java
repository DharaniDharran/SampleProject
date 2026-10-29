package com.hotel.repo;

import com.hotel.models.Review;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewRepository {
    private final Map<Integer, Review> reviews = new HashMap<>();

    public void save(Review review) {
        reviews.put(review.getReviewId(), review);
    }

    public Review findById(int id) {
        return reviews.get(id);
    }

    public Review findByOrderId(int orderId) {
        for (Review r : reviews.values()) {
            if (r.getOrderId() == orderId) {
                return r;
            }
        }
        return null;
    }

    public List<Review> findAll() {
        return new ArrayList<>(reviews.values());
    }

    public List<Review> findByCustomerId(int customerId) {
        List<Review> list = new ArrayList<>();
        for (Review r : reviews.values()) {
            if (r.getCustomerId() == customerId) {
                list.add(r);
            }
        }
        return list;
    }
}
