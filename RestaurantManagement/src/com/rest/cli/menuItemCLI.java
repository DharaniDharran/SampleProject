package com.rest.cli;

import com.rest.models.menuItem;
import com.rest.services.menuItemService;
import java.util.List;
import java.util.Scanner;

public class menuItemCLI {
    private final menuItemService service;
    private final Scanner scanner;

    public menuItemCLI(menuItemService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public menuItemCLI(menuItemService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n==MENU MANAGEMENT==");
            System.out.println("1. View all menu items");
            System.out.println("2. Filter by category");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                List<menuItem> items = service.getAllMenuItems();
                System.out.println("\n==ALL MENU ITEMS==");
                for (menuItem item : items) {
                    System.out.println(item);
                }
            } else if (choice == 2) {
                System.out.print("Enter category (Starters/Rice/Main Course/Beverages): ");
                String category = scanner.nextLine();
                List<menuItem> items = service.getMenuItemsByCategory(category);
                System.out.println("\n==ITEMS IN " + category.toUpperCase() + "==");
                if (items.isEmpty()) {
                    System.out.println("No items found in this category.");
                } else {
                    for (menuItem item : items) {
                        System.out.println(item);
                    }
                }
            } else if (choice == 3 || choice == 0) {
                break;
            } else {
                System.out.println("Invalid Choice");
            }
        }
    }
}
