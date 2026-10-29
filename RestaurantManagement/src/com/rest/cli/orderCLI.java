package com.rest.cli;

import com.rest.models.menuItem;
import com.rest.models.order;
import com.rest.models.orderItem;
import com.rest.services.menuItemService;
import com.rest.services.orderService;
import java.util.List;
import java.util.Scanner;

public class orderCLI {
    private final orderService ordService;
    private final menuItemService menuService;
    private final Scanner scanner;

    private int nextOrderId = 1;
    private int nextOrderItemId = 1;

    public orderCLI(orderService ordService, menuItemService menuService) {
        this.ordService = ordService;
        this.menuService = menuService;
        this.scanner = new Scanner(System.in);
    }

    public orderCLI(orderService ordService, menuItemService menuService, Scanner scanner) {
        this.ordService = ordService;
        this.menuService = menuService;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n==ORDER MANAGEMENT==");
            System.out.println("1. Place new order for a table");
            System.out.println("2. Add items to existing order");
            System.out.println("3. View all orders");
            System.out.println("4. View order details & bill");
            System.out.println("5. Update order status");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter Table ID: ");
                int tableId = scanner.nextInt();
                scanner.nextLine();

                int orderId = nextOrderId++;
                order ord = ordService.createOrder(orderId, tableId);
                if (ord != null) {
                    System.out.println("Order created with ID: " + orderId);
                    addItemsLoop(orderId);
                    System.out.println("Order #" + orderId + " placed successfully!");
                }
            } else if (choice == 2) {
                System.out.print("Enter Order ID to add items to: ");
                int orderId = scanner.nextInt();
                scanner.nextLine();

                order ord = ordService.getOrderById(orderId);
                if (ord == null) {
                    System.out.println("Error: Order #" + orderId + " not found!");
                } else if (ord.getStatus() == order.OrderStatus.COMPLETED) {
                    System.out.println("Error: Cannot add items to a COMPLETED order.");
                } else {
                    addItemsLoop(orderId);
                    System.out.println("Items added to Order #" + orderId + " successfully!");
                }
            } else if (choice == 3) {
                List<order> orders = ordService.getAllOrders();
                System.out.println("\n==ALL ORDERS==");
                if (orders.isEmpty()) {
                    System.out.println("No orders placed yet.");
                } else {
                    for (order o : orders) {
                        System.out.println(o);
                    }
                }
            } else if (choice == 4) {
                System.out.print("Enter Order ID: ");
                int orderId = scanner.nextInt();
                scanner.nextLine();

                order ord = ordService.getOrderById(orderId);
                if (ord == null) {
                    System.out.println("Order not found!");
                } else {
                    System.out.println("\n" + ord);
                    System.out.println("-- Order Items --");
                    List<orderItem> items = ordService.getOrderItems(orderId);
                    for (orderItem it : items) {
                        menuItem mi = menuService.getMenuItemById(it.getMenuitem_id());
                        String name = (mi != null) ? mi.getName() : "Unknown";
                        System.out.printf("- %s x %d @ $%.2f = $%.2f%n", 
                            name, it.getQuantity(), it.getUnitwise(), it.getTotalPrice());
                    }
                    System.out.printf("Total Amount: $%.2f%n", ordService.calculateTotal(orderId));
                }
            } else if (choice == 5) {
                System.out.print("Enter Order ID: ");
                int orderId = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Select Status: 1. PLACED  2. PREPARING  3. SERVED  4. COMPLETED");
                System.out.print("Choice: ");
                int stChoice = scanner.nextInt();
                scanner.nextLine();

                order.OrderStatus status = null;
                switch (stChoice) {
                    case 1: status = order.OrderStatus.PLACED; break;
                    case 2: status = order.OrderStatus.PREPARING; break;
                    case 3: status = order.OrderStatus.SERVED; break;
                    case 4: status = order.OrderStatus.COMPLETED; break;
                    default: System.out.println("Invalid status choice.");
                }

                if (status != null) {
                    if (ordService.updateOrderStatus(orderId, status)) {
                        System.out.println("Order #" + orderId + " updated to " + status);
                        if (status == order.OrderStatus.COMPLETED) {
                            System.out.println("Table has been freed automatically.");
                        }
                    } else {
                        System.out.println("Order not found.");
                    }
                }
            } else if (choice == 6 || choice == 0) {
                break;
            } else {
                System.out.println("Invalid Choice");
            }
        }
    }

    private void addItemsLoop(int orderId) {
        while (true) {
            System.out.println("\nAvailable Menu Items:");
            for (menuItem mi : menuService.getAllMenuItems()) {
                System.out.printf("%d. %s - $%.2f (%s)%n", 
                    mi.getId(), mi.getName(), mi.getPrice(), mi.getCategory());
            }
            System.out.print("Enter MenuItem ID to add (or 0 to finish): ");
            int menuId = scanner.nextInt();
            scanner.nextLine();

            if (menuId == 0) {
                break;
            }

            System.out.print("Enter Quantity: ");
            int qty = scanner.nextInt();
            scanner.nextLine();

            if (qty <= 0) {
                System.out.println("Quantity must be greater than 0.");
                continue;
            }

            orderItem item = ordService.addItemToOrder(nextOrderItemId++, orderId, menuId, qty);
            if (item != null) {
                System.out.println("Added: " + item);
            }
        }
    }
}
