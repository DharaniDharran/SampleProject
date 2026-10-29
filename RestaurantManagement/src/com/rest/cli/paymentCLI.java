package com.rest.cli;

import com.rest.Payment.CardPayment;
import com.rest.Payment.CashPayment;
import com.rest.Payment.PaymentMethod;
import com.rest.Payment.UPIPayment;
import com.rest.models.order;
import com.rest.models.payment;
import com.rest.services.orderService;
import com.rest.services.paymentService;
import java.util.List;
import java.util.Scanner;

public class paymentCLI {
    private final paymentService payService;
    private final orderService ordService;
    private final Scanner scanner;
    private int nextPaymentId = 1;

    public paymentCLI(paymentService payService, orderService ordService) {
        this.payService = payService;
        this.ordService = ordService;
        this.scanner = new Scanner(System.in);
    }

    public paymentCLI(paymentService payService, orderService ordService, Scanner scanner) {
        this.payService = payService;
        this.ordService = ordService;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n==PAYMENT MANAGEMENT==");
            System.out.println("1. Pay for an order");
            System.out.println("2. View all payments");
            System.out.println("3. View payments for an order");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter Order ID to pay: ");
                int orderId = scanner.nextInt();
                scanner.nextLine();

                order ord = ordService.getOrderById(orderId);
                if (ord == null) {
                    System.out.println("Error: Order #" + orderId + " not found!");
                    continue;
                }

                double total = ordService.calculateTotal(orderId);
                System.out.printf("Order #%d Total: $%.2f%n", orderId, total);

                if (total <= 0) {
                    System.out.println("Cannot pay for an empty order. Please add items first.");
                    continue;
                }

                if (ord.getStatus() == order.OrderStatus.COMPLETED) {
                    System.out.println("Notice: This order has already been marked as COMPLETED.");
                }

                System.out.println("Select Payment Method:");
                System.out.println("1. Cash");
                System.out.println("2. Credit / Debit Card");
                System.out.println("3. UPI");
                System.out.print("Enter Method: ");

                int methodChoice = scanner.nextInt();
                scanner.nextLine();

                PaymentMethod method;
                switch (methodChoice) {
                    case 1:
                        method = new CashPayment();
                        break;
                    case 2:
                        method = new CardPayment();
                        break;
                    case 3:
                        method = new UPIPayment();
                        break;
                    default:
                        System.out.println("Invalid payment method.");
                        continue;
                }

                payment result = payService.processPayment(nextPaymentId++, orderId, method);
                if (result != null) {
                    System.out.println("Payment Record: " + result);
                }
            } else if (choice == 2) {
                List<payment> payments = payService.getAllPayments();
                System.out.println("\n==ALL PAYMENTS==");
                if (payments.isEmpty()) {
                    System.out.println("No payments recorded yet.");
                } else {
                    for (payment p : payments) {
                        System.out.println(p);
                    }
                }
            } else if (choice == 3) {
                System.out.print("Enter Order ID: ");
                int orderId = scanner.nextInt();
                scanner.nextLine();

                List<payment> payments = payService.getPaymentsByOrderId(orderId);
                System.out.println("\n==PAYMENTS FOR ORDER #" + orderId + "==");
                if (payments.isEmpty()) {
                    System.out.println("No payments found for this order.");
                } else {
                    for (payment p : payments) {
                        System.out.println(p);
                    }
                }
            } else if (choice == 4 || choice == 0) {
                break;
            } else {
                System.out.println("Invalid Choice. Please try again.");
            }
        }
    }
}
