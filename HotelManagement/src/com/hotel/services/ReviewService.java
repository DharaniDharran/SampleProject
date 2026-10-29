package com.hotel.services;

import com.hotel.models.Order;
import com.hotel.models.OrderStatus;
import com.hotel.models.Restaurant;
import com.hotel.models.Review;
import com.hotel.repo.OrderRepository;
import com.hotel.repo.RestaurantRepository;
import com.hotel.repo.ReviewRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewService {
    public static final int MAX_REVIEW_WINDOW_HOURS = 48;

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private int nextReviewId = 1;

    public ReviewService(ReviewRepository reviewRepository,
                         OrderRepository orderRepository,
                         RestaurantRepository restaurantRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Review submitReview(int orderId, int customerId, int restaurantRating, int deliveryRating, String comment) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        if (order.getCustomerId() != customerId) {
            throw new SecurityException("You can only review your own orders.");
        }

        // Rule: A customer can rate only after the order is Delivered.
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Reviews can only be submitted for DELIVERED orders. Current order status: " + order.getStatus());
        }

        // Rule: Each order can be rated only once.
        if (reviewRepository.findByOrderId(orderId) != null) {
            throw new IllegalStateException("Order #" + orderId + " has already been reviewed.");
        }

        // Rule: Only within 48 hours of delivery.
        if (order.getDeliveredAt() != null) {
            long hoursSinceDelivery = Duration.between(order.getDeliveredAt(), LocalDateTime.now()).toHours();
            if (hoursSinceDelivery > MAX_REVIEW_WINDOW_HOURS) {
                throw new IllegalStateException("Review window expired. Ratings can only be submitted within " + MAX_REVIEW_WINDOW_HOURS + " hours of delivery.");
            }
        }

        Review review = new Review(nextReviewId++, orderId, customerId, restaurantRating, deliveryRating, comment);
        reviewRepository.save(review);

        // Rule: A restaurant's average rating is recalculated after every new rating.
        // A restaurant with average rating below 2.0 after at least 10 ratings is flagged for admin review.
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId());
        if (restaurant != null) {
            restaurant.addRating(restaurantRating);
            System.out.printf("[Review System] Review recorded. Restaurant '%s' new average rating: %.2f (%d reviews). %s%n",
                    restaurant.getName(), restaurant.getAverageRating(), restaurant.getRatingCount(),
                    restaurant.isFlaggedForReview() ? "[ALERT: FLAGGED FOR ADMIN REVIEW]" : "");
        }

        return review;
    }

    public Review getReviewByOrderId(int orderId) {
        return reviewRepository.findByOrderId(orderId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
