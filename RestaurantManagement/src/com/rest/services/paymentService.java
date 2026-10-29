package com.rest.services;

import com.rest.Payment.PaymentMethod;
import com.rest.models.order;
import com.rest.models.payment;
import com.rest.repository.paymentRepository;
import java.util.List;

public class paymentService {
    private final paymentRepository repository;
    private final orderService ordService;

    public paymentService(paymentRepository repository, orderService ordService) {
        this.repository = repository;
        this.ordService = ordService;
    }

    public paymentService(orderService ordService) {
        this(new paymentRepository(), ordService);
    }

    public payment processPayment(int paymentId, int orderId, PaymentMethod method) {
        order ord = ordService.getOrderById(orderId);
        if (ord == null) {
            System.out.println("Error: Order ID " + orderId + " not found.");
            return null;
        }

        if (ord.getStatus() == order.OrderStatus.COMPLETED) {
            System.out.println("Warning: Order ID " + orderId + " is already completed/paid.");
        }

        double amount = ordService.calculateTotal(orderId);
        if (amount <= 0) {
            System.out.println("Error: Order total is 0. Add items before processing payment.");
            return null;
        }

        boolean success = method.pay(amount);
        String status = success ? "SUCCESS" : "FAILED";

        payment pay = new payment(paymentId, orderId, amount, method.getMethodName(), status);
        repository.save(pay);

        if (success) {
            ordService.updateOrderStatus(orderId, order.OrderStatus.COMPLETED);
            System.out.println("Payment recorded successfully. Order #" + orderId + " is now COMPLETED and table freed.");
        }

        return pay;
    }

    public List<payment> getAllPayments() {
        return repository.findAll();
    }

    public payment getPaymentById(int id) {
        return repository.findById(id);
    }

    public List<payment> getPaymentsByOrderId(int orderId) {
        return repository.findByOrderId(orderId);
    }
}
