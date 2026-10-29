package com.hotel.repo;

import com.hotel.models.Cart;
import java.util.HashMap;
import java.util.Map;

public class CartRepository {
    private final Map<Integer, Cart> cartsByCustomerId = new HashMap<>();
    private int nextCartId = 1;

    public Cart getOrCreateCart(int customerId) {
        if (!cartsByCustomerId.containsKey(customerId)) {
            Cart cart = new Cart(nextCartId++, customerId);
            cartsByCustomerId.put(customerId, cart);
        }
        return cartsByCustomerId.get(customerId);
    }

    public Cart findByCustomerId(int customerId) {
        return cartsByCustomerId.get(customerId);
    }

    public void removeByCustomerId(int customerId) {
        cartsByCustomerId.remove(customerId);
    }
}
