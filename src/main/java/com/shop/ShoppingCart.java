package com.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private static final double TAX_RATE = 0.06;
    private static final double STANDARD_SHIPPING = 10.00;
    private static final double NEXT_DAY_SHIPPING = 25.00;
    private static final double FREE_SHIPPING_THRESHOLD = 50.00;
    private static final double MIN_PURCHASE = 1.00;
    private static final double MAX_PURCHASE = 99999.99;

    private List<Item> items;
    private State state;
    private ShippingType shippingType;

    public ShoppingCart(State state, ShippingType shippingType) {
        this.items = new ArrayList<>();
        this.state = state;
        this.shippingType = shippingType;
    }

    public int addItem(Item item) {
        if (item.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        items.add(item);
        return items.size();
    }

    public void removeItem(String name) {
        boolean removed = items.removeIf(i -> i.getName().equalsIgnoreCase(name));
        if (!removed) {
            throw new IllegalArgumentException("Item not found: " + name);
        }
    }

    public void editQuantity(String name, int newQuantity) {
        if (newQuantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                item.setQuantity(newQuantity);
                return;
            }
        }
        throw new IllegalArgumentException("Item not found: " + name);
    }

    public double getRawTotal() {
        double total = 0;
        for (Item item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double getTax() {
        if (state == State.IL || state == State.CA || state == State.NY) {
            return getRawTotal() * TAX_RATE;
        }
        return 0.0;
    }

    public double getShippingCost() {
        if (shippingType == ShippingType.NEXT_DAY) {
            return NEXT_DAY_SHIPPING;
        }
        // STANDARD: free if raw total > $50
        if (getRawTotal() > FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return STANDARD_SHIPPING;
    }

    public double getTotal() {
        double total = getRawTotal() + getTax() + getShippingCost();
        if (total < MIN_PURCHASE) {
            throw new IllegalStateException("Purchase amount is below minimum of $1.00.");
        }
        if (total > MAX_PURCHASE) {
            throw new IllegalStateException("Purchase amount exceeds maximum of $99,999.99.");
        }
        return total;
    }

    public String checkout() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart.");
        }
        getTotal(); // validates min/max
        items.clear();
        return "transaction completed";
    }

    public List<Item> getItems() {
        return items;
    }

    public State getState() { return state; }
    public ShippingType getShippingType() { return shippingType; }
}