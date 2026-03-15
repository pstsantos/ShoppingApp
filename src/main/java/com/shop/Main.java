package com.shop;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to the Shopping App ===");

        // Setup: choose state
        State state = chooseState();
        ShippingType shippingType = chooseShipping();
        ShoppingCart cart = new ShoppingCart(state, shippingType);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addItem(cart);
                case "2" -> viewCart(cart);
                case "3" -> showTotal(cart);
                case "4" -> editQuantity(cart);
                case "5" -> removeItem(cart);
                case "6" -> {
                    running = !checkout(cart);
                }
                case "7" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    static void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Add item");
        System.out.println("2. View cart");
        System.out.println("3. Get current total");
        System.out.println("4. Edit item quantity");
        System.out.println("5. Remove item");
        System.out.println("6. Checkout");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    static State chooseState() {
        System.out.println("\nEnter your state (IL, CA, NY, or OTHER): ");
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return State.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid state. Enter IL, CA, NY, or OTHER: ");
            }
        }
    }

    static ShippingType chooseShipping() {
        System.out.print("Enter shipping type (STANDARD or NEXT_DAY): ");
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return ShippingType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid shipping type. Enter STANDARD or NEXT_DAY: ");
            }
        }
    }

    static void addItem(ShoppingCart cart) {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine().trim();

        double price = 0;
        while (true) {
            System.out.print("Enter price: ");
            String input = scanner.nextLine().trim();
            try {
                price = Double.parseDouble(input);
                if (price <= 0) {
                    System.out.println("Price must be greater than 0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price. Enter a number like 2.99.");
            }
        }

        int quantity = 0;
        while (true) {
            System.out.print("Enter quantity (must be a whole number >= 1): ");
            String input = scanner.nextLine().trim();
            try {
                quantity = Integer.parseInt(input);
                if (quantity < 1) {
                    System.out.println("Quantity must be at least 1.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Must be a whole number (e.g. 2, not 2.5).");
            }
        }

        int count = cart.addItem(new Item(name, price, quantity));
        System.out.println("✅ Item added! Cart now has " + count + " item(s).");
    }

    static void viewCart(ShoppingCart cart) {
        List<Item> items = cart.getItems();
        if (items.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }
        System.out.println("\n--- Cart Contents ---");
        for (Item item : items) {
            System.out.println("  " + item);
        }
    }

    static void showTotal(ShoppingCart cart) {
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        try {
            System.out.printf("Raw total:  $%.2f%n", cart.getRawTotal());
            System.out.printf("Tax:        $%.2f%n", cart.getTax());
            System.out.printf("Shipping:   $%.2f%n", cart.getShippingCost());
            System.out.printf("Grand total:$%.2f%n", cart.getTotal());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void editQuantity(ShoppingCart cart) {
        System.out.print("Enter item name to edit: ");
        String name = scanner.nextLine().trim();
        while (true) {
            System.out.print("Enter new quantity (whole number >= 1): ");
            String input = scanner.nextLine().trim();
            try {
                int qty = Integer.parseInt(input);
                cart.editQuantity(name, qty);
                System.out.println("✅ Quantity updated.");
                return;
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Must be a whole number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
        }
    }

    static void removeItem(ShoppingCart cart) {
        System.out.print("Enter item name to remove: ");
        String name = scanner.nextLine().trim();
        try {
            cart.removeItem(name);
            System.out.println("✅ Item removed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static boolean checkout(ShoppingCart cart) {
        try {
            String result = cart.checkout();
            System.out.println("✅ " + result);
            return true;
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}