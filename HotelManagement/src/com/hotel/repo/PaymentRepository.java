package com.hotel.repo;

import com.hotel.models.Coupon;
import com.hotel.models.CouponUsage;
import com.hotel.models.Payment;
import com.hotel.models.PaymentAttempt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentRepository {
    private final Map<Integer, Payment> payments = new HashMap<>();
    private final List<PaymentAttempt> paymentAttempts = new ArrayList<>();
    private final Map<String, Coupon> couponsByCode = new HashMap<>();
    private final List<CouponUsage> couponUsages = new ArrayList<>();
    private int nextAttemptId = 1;
    private int nextUsageId = 1;

    public void savePayment(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    public Payment findPaymentById(int paymentId) {
        return payments.get(paymentId);
    }

    public Payment findPaymentByOrderId(int orderId) {
        for (Payment p : payments.values()) {
            if (p.getOrderId() == orderId) {
                return p;
            }
        }
        return null;
    }

    public List<Payment> findAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public void recordAttempt(int paymentId, int attemptNumber, String status) {
        PaymentAttempt attempt = new PaymentAttempt(nextAttemptId++, paymentId, attemptNumber, status);
        paymentAttempts.add(attempt);
    }

    public List<PaymentAttempt> getAttemptsByPaymentId(int paymentId) {
        List<PaymentAttempt> list = new ArrayList<>();
        for (PaymentAttempt pa : paymentAttempts) {
            if (pa.getPaymentId() == paymentId) {
                list.add(pa);
            }
        }
        return list;
    }

    public void saveCoupon(Coupon coupon) {
        couponsByCode.put(coupon.getCode().toUpperCase(), coupon);
    }

    public Coupon findCouponByCode(String code) {
        if (code == null) return null;
        return couponsByCode.get(code.toUpperCase());
    }

    public List<Coupon> findAllCoupons() {
        return new ArrayList<>(couponsByCode.values());
    }

    public boolean hasCustomerUsedCoupon(int customerId, int couponId) {
        for (CouponUsage cu : couponUsages) {
            if (cu.getCustomerId() == customerId && cu.getCouponId() == couponId) {
                return true;
            }
        }
        return false;
    }

    public void recordCouponUsage(int couponId, int customerId, int orderId) {
        CouponUsage usage = new CouponUsage(nextUsageId++, couponId, customerId, orderId);
        couponUsages.add(usage);
    }
}
