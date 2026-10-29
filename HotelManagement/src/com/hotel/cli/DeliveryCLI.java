package com.hotel.cli;

import com.hotel.models.Delivery;
import com.hotel.models.DeliveryPartner;
import com.hotel.models.DeliveryStatus;
import com.hotel.models.Order;
import com.hotel.models.User;
import com.hotel.models.UserRole;
import com.hotel.services.DeliveryService;
import com.hotel.services.OrderService;
import com.hotel.services.UserService;
import java.util.List;
import java.util.Scanner;

public class DeliveryCLI {
    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final UserService userService;
    private final Scanner scanner;

    private DeliveryPartner activePartner;

    public DeliveryCLI(DeliveryService deliveryService,
                       OrderService orderService,
                       UserService userService,
                       Scanner scanner) {
        this.deliveryService = deliveryService;
        this.orderService = orderService;
        this.userService = userService;
        this.scanner = scanner;
    }

    public void setActivePartner(DeliveryPartner partner) {
        this.activePartner = partner;
    }

    public void start() {
        if (activePartner == null) {
            selectOrCreatePartner();
        }

        while (scanner.hasNextLine()) {
            System.out.println("\n==================================");
            System.out.printf("   DELIVERY PARTNER PORTAL: %s (ID: %d)%n", activePartner.getName(), activePartner.getUserId());
            System.out.printf("   City: %s | Status: %s | Daily Rejections: %d/3 | Earnings: Rs.%.2f%n",
                    activePartner.getCity(), activePartner.getStatus(), activePartner.getDailyRejections(), activePartner.getTotalEarnings());
            System.out.println("==================================");
            System.out.println("1. View Assigned Deliveries");
            System.out.println("2. Reject Current Assignment (Max 3/day)");
            System.out.println("3. Mark Order as PICKED UP (Out For Delivery)");
            System.out.println("4. Mark Order as DELIVERED (+Rs. 30 Earning)");
            System.out.println("5. View Earnings & Delivery History");
            System.out.println("6. Switch / Register Delivery Partner");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter Choice: ");

            String choiceStr = scanner.nextLine().trim();
            if (choiceStr.isEmpty()) continue;
            int choice = -1;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        viewAssignedDeliveries();
                        break;
                    case 2:
                        rejectAssignmentUI();
                        break;
                    case 3:
                        markPickedUpUI();
                        break;
                    case 4:
                        markDeliveredUI();
                        break;
                    case 5:
                        viewEarningsHistory();
                        break;
                    case 6:
                        selectOrCreatePartner();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select an option from the menu.");
                }
            } catch (Exception ex) {
                System.out.println("\n[ERROR] " + ex.getMessage());
            }
        }
    }

    private void selectOrCreatePartner() {
        System.out.println("\n--- Select / Register Delivery Partner ---");
        List<User> partners = userService.getUsersByRole(UserRole.PARTNER);
        if (!partners.isEmpty()) {
            System.out.println("Existing Delivery Partners:");
            for (User u : partners) {
                DeliveryPartner dp = (DeliveryPartner) u;
                System.out.printf("  [%d] %s (City: %s, Status: %s, Earnings: Rs.%.2f)%n",
                        dp.getUserId(), dp.getName(), dp.getCity(), dp.getStatus(), dp.getTotalEarnings());
            }
            System.out.println("  [0] Register a New Partner");
            System.out.print("Select Partner ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (id > 0) {
                User u = userService.getUserById(id);
                if (u instanceof DeliveryPartner) {
                    activePartner = (DeliveryPartner) u;
                    System.out.println("Logged in as " + activePartner.getName());
                    return;
                }
            }
        }

        System.out.print("Enter Partner Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Operating City (e.g. Bangalore / Mumbai): ");
        String city = scanner.nextLine().trim();

        activePartner = userService.registerDeliveryPartner(name, phone, email, city);
        System.out.println("Delivery Partner registered successfully with ID: " + activePartner.getUserId());
    }

    private void viewAssignedDeliveries() {
        List<Delivery> list = deliveryService.getDeliveriesByPartner(activePartner.getUserId());
        System.out.println("\n=== ASSIGNED DELIVERIES ===");
        if (list.isEmpty()) {
            System.out.println("No deliveries assigned to you currently.");
            return;
        }
        for (Delivery d : list) {
            Order o = orderService.getOrderById(d.getOrderId());
            System.out.printf("%s | Order Details: %s%n", d, (o != null ? o.toString() : ""));
        }
    }

    private void rejectAssignmentUI() {
        System.out.print("Enter Delivery ID to reject: ");
        int dId = Integer.parseInt(scanner.nextLine().trim());
        deliveryService.rejectAssignment(dId, activePartner.getUserId());
    }

    private void markPickedUpUI() {
        System.out.print("Enter Delivery ID to mark PICKED UP: ");
        int dId = Integer.parseInt(scanner.nextLine().trim());
        deliveryService.markOrderPickedUp(dId, activePartner.getUserId());
    }

    private void markDeliveredUI() {
        System.out.print("Enter Delivery ID to mark DELIVERED: ");
        int dId = Integer.parseInt(scanner.nextLine().trim());
        deliveryService.markOrderDelivered(dId, activePartner.getUserId());
    }

    private void viewEarningsHistory() {
        System.out.printf("%n=== EARNINGS SUMMARY FOR %s ===%n", activePartner.getName());
        System.out.printf("Total Deliveries Completed: Rs. %.2f earned.%n", activePartner.getTotalEarnings());
        List<com.hotel.models.DeliveryEarnings> earnings = deliveryService.getDeliveryRepository().getEarningsByPartnerId(activePartner.getUserId());
        for (com.hotel.models.DeliveryEarnings de : earnings) {
            System.out.println(" - " + de);
        }
    }
}
