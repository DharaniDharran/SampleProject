package com.hotel.services;

import com.hotel.models.Cart;
import com.hotel.models.CartItem;
import com.hotel.models.MenuItem;
import com.hotel.repo.CartRepository;
import com.hotel.repo.MenuItemRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartService {
    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private int nextCartItemId = 1;

    public CartService(CartRepository cartRepository, MenuItemRepository menuItemRepository) {
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Cart getCart(int customerId) {
        return cartRepository.getOrCreateCart(customerId);
    }

    public CartItem addItemToCart(int customerId, int menuItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        MenuItem menuItem = menuItemRepository.findById(menuItemId);
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item not found with ID: " + menuItemId);
        }

        // Rule: A menu item marked Unavailable cannot be added to a cart.
        if (!menuItem.isAvailable()) {
            throw new IllegalStateException("Cannot add '" + menuItem.getName() + "' to cart: Item is currently UNAVAILABLE.");
        }

        Cart cart = cartRepository.getOrCreateCart(customerId);

        // Rule: A cart can contain items from only one restaurant at a time.
        if (cart.getRestaurantId() != null && cart.getRestaurantId() != menuItem.getRestaurantId()) {
            throw new IllegalStateException(
                    "Your cart already contains items from Restaurant #" + cart.getRestaurantId() +
                    ". Please clear your cart first before ordering from Restaurant #" + menuItem.getRestaurantId() + ".");
        }

        CartItem existing = cart.getItemByMenuItemId(menuItemId);
        int newTotalQty = (existing != null ? existing.getQuantity() : 0) + quantity;

        // Rule: A maximum of 10 units of the same item can be added to a cart.
        if (newTotalQty > 10) {
            throw new IllegalArgumentException("Cannot add " + quantity + " units. Maximum 10 units of '" + menuItem.getName() + "' allowed per cart.");
        }

        if (cart.getRestaurantId() == null) {
            cart.setRestaurantId(menuItem.getRestaurantId());
        }

        if (existing != null) {
            existing.setQuantity(newTotalQty);
            cart.setUpdatedAt(LocalDateTime.now());
            return existing;
        } else {
            CartItem cartItem = new CartItem(nextCartItemId++, cart.getCartId(), menuItemId, quantity);
            cart.getItems().add(cartItem);
            cart.setUpdatedAt(LocalDateTime.now());
            return cartItem;
        }
    }

    public void removeItemFromCart(int customerId, int menuItemId) {
        Cart cart = cartRepository.getOrCreateCart(customerId);
        cart.removeItem(menuItemId);
    }

    public void clearCart(int customerId) {
        Cart cart = cartRepository.getOrCreateCart(customerId);
        cart.clear();
    }

    public double calculateCartSubtotal(int customerId) {
        Cart cart = cartRepository.getOrCreateCart(customerId);
        double total = 0.0;
        for (CartItem ci : cart.getItems()) {
            MenuItem item = menuItemRepository.findById(ci.getItemId());
            if (item != null) {
                total += item.getPrice() * ci.getQuantity();
            }
        }
        return total;
    }

    /**
     * Rule: If an item becomes unavailable after it was added to a cart,
     * it is removed at checkout and the customer is notified.
     * Returns list of item names that were removed.
     */
    public List<String> sanitizeCartForCheckout(int customerId) {
        Cart cart = cartRepository.getOrCreateCart(customerId);
        List<String> removedItemNames = new ArrayList<>();

        Iterator<CartItem> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            CartItem ci = iterator.next();
            MenuItem item = menuItemRepository.findById(ci.getItemId());
            if (item == null || !item.isAvailable()) {
                removedItemNames.add(item != null ? item.getName() : "Item #" + ci.getItemId());
                iterator.remove();
            }
        }

        if (cart.getItems().isEmpty()) {
            cart.setRestaurantId(null);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        return removedItemNames;
    }
}
