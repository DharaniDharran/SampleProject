package com.hotel.services;

import com.hotel.models.Coupon;
import com.hotel.models.Order;
import com.hotel.models.Payment;
import com.hotel.models.PaymentStatus;
import com.hotel.payment.CODPayment;
import com.hotel.payment.CardPayment;
import com.hotel.payment.PaymentMethod;
import com.hotel.payment.UPIPayment;
import com.hotel.repo.PaymentRepository;
import java.time.LocalDate;
import java.util.List;

public class PaymentService {
    public static final double DELIVERY_FEE = 40.0;
    public static final double FREE_DELIVERY_THRESHOLD = 500.0;
    public static final double GST_RATE = 0.05;

    private final PaymentRepository paymentRepository;
    private int nextPaymentId = 1;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentRepository getPaymentRepository() {
        return paymentRepository;
    }

    public double calculateDeliveryFee(double itemsTotal) {
        return itemsTotal >= FREE_DELIVERY_THRESHOLD ? 0.0 : DELIVERY_FEE;
    }

    public double calculateGst(double itemsTotal) {
        return Math.round((itemsTotal * GST_RATE) * 100.0) / 100.0;
    }

    public Coupon validateAndGetCoupon(String couponCode, int customerId, double itemsTotal) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return null;
        }

        Coupon coupon = paymentRepository.findCouponByCode(couponCode.trim());
        if (coupon == null) {
            throw new IllegalArgumentException("Invalid coupon code '" + couponCode + "'.");
        }

        if (coupon.isExpired()) {
            throw new IllegalStateException("Coupon '" + couponCode + "' expired on " + coupon.getExpiryDate() + ".");
        }

        if (itemsTotal < coupon.getMinimumOrderValue()) {
            throw new IllegalStateException(String.format("Coupon '%s' requires a minimum order value of Rs. %.2f. Current items total: Rs. %.2f.",
                    couponCode, coupon.getMinimumOrderValue(), itemsTotal));
        }

        if (paymentRepository.hasCustomerUsedCoupon(customerId, coupon.getCouponId())) {
            throw new IllegalStateException("Customer has already used coupon '" + couponCode + "'. Coupons can only be used once per customer.");
        }

        return coupon;
    }

    public Payment processOrderPayment(int orderId, int customerId, String paymentMethodName,
                                       double amount, String paymentDetails) {
        PaymentMethod method;
        PaymentStatus initialStatus;

        if (paymentMethodName.equalsIgnoreCase("UPI")) {
            method = new UPIPayment();
            initialStatus = PaymentStatus.PAID;
        } else if (paymentMethodName.equalsIgnoreCase("CARD")) {
            method = new CardPayment();
            initialStatus = PaymentStatus.PAID;
        } else if (paymentMethodName.equalsIgnoreCase("COD")) {
            method = new CODPayment();
            initialStatus = PaymentStatus.PENDING;
        } else {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethodName);
        }

        boolean success = method.processPayment(amount, paymentDetails);
        int paymentId = nextPaymentId++;

        if (!success) {
            Payment failedPayment = new Payment(paymentId, orderId, method.getMethodName(), amount, PaymentStatus.FAILED);
            paymentRepository.savePayment(failedPayment);
            paymentRepository.recordAttempt(paymentId, 1, "FAILED");
            throw new IllegalStateException("Payment failed. You have 15 minutes to retry before your cart is released.");
        }

        Payment payment = new Payment(paymentId, orderId, method.getMethodName(), amount, initialStatus);
        paymentRepository.savePayment(payment);
        paymentRepository.recordAttempt(paymentId, 1, "SUCCESS");

        return payment;
    }

    public void processRefund(int orderId, String reason) {
        Payment payment = paymentRepository.findPaymentByOrderId(orderId);
        if (payment == null) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            payment.setStatus(PaymentStatus.REFUNDED);
            System.out.printf("[Refund Engine] Full refund of Rs. %.2f issued for Order #%d via %s. Reason: %s%n",
                    payment.getAmount(), orderId, payment.getPaymentMethod(), reason);
        } else if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.REFUNDED);
            System.out.printf("[Payment Engine] COD payment pending for Order #%d cancelled. Reason: %s%n",
                    orderId, reason);
        }
    }

    public void recordCouponUsage(int couponId, int customerId, int orderId) {
        paymentRepository.recordCouponUsage(couponId, customerId, orderId);
    }

    public void addCoupon(String code, com.hotel.models.DiscountType discountType,
                          double discountValue, double minOrderValue, LocalDate expiryDate) {
        Coupon coupon = new Coupon(paymentRepository.findAllCoupons().size() + 1,
                code, discountType, discountValue, minOrderValue, expiryDate);
        paymentRepository.saveCoupon(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return paymentRepository.findAllCoupons();
    }

    public Payment getPaymentByOrderId(int orderId) {
        return paymentRepository.findPaymentByOrderId(orderId);
    }
}
