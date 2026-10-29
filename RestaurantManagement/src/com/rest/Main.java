package com.rest;

import com.rest.cli.menuItemCLI;
import com.rest.cli.orderCLI;
import com.rest.cli.paymentCLI;
import com.rest.cli.tableCLI;
import com.rest.services.menuItemService;
import com.rest.services.orderItemService;
import com.rest.services.orderService;
import com.rest.services.paymentService;
import com.rest.services.tableService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        tableService tblService = new tableService();
        menuItemService menuService = new menuItemService();
        orderItemService itemService = new orderItemService();
        orderService ordService = new orderService(itemService, menuService, tblService);
        paymentService payService = new paymentService(ordService);

        tableCLI tblCLI = new tableCLI(tblService, scanner);
        menuItemCLI menuCLI = new menuItemCLI(menuService, scanner);
        orderCLI ordCLI = new orderCLI(ordService, menuService, scanner);
        paymentCLI payCLI = new paymentCLI(payService, ordService, scanner);

        while (true) {
            System.out.println("\n==================================");
            System.out.println("   RESTAURANT MANAGEMENT SYSTEM   ");
            System.out.println("==================================");
            System.out.println("1. Table Management");
            System.out.println("2. Menu Management");
            System.out.println("3. Order Management");
            System.out.println("4. Payment Management");
            System.out.println("5. Exit");
            System.out.print("Enter Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                tblCLI.start();
            } else if (choice == 2) {
                menuCLI.start();
            } else if (choice == 3) {
                ordCLI.start();
            } else if (choice == 4) {
                payCLI.start();
            } else if (choice == 5 || choice == 0) {
                System.out.println("Thank you for using Restaurant Management System!");
                break;
            } else {
                System.out.println("Invalid Choice. Please try again.");
            }
        }

        scanner.close();
    }
}
