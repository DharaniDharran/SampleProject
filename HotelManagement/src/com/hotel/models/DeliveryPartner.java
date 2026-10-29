package com.hotel.models;

public class DeliveryPartner extends User {
    private String city;
    private PartnerStatus status;
    private int dailyRejections;
    private double totalEarnings;

    public DeliveryPartner(int userId, String name, String phone, String email, String city) {
        super(userId, name, phone, email, UserRole.PARTNER);
        this.city = city;
        this.status = PartnerStatus.AVAILABLE;
        this.dailyRejections = 0;
        this.totalEarnings = 0.0;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public PartnerStatus getStatus() {
        return status;
    }

    public void setStatus(PartnerStatus status) {
        this.status = status;
    }

    public int getDailyRejections() {
        return dailyRejections;
    }

    public void incrementDailyRejections() {
        this.dailyRejections++;
    }

    public void resetDailyRejections() {
        this.dailyRejections = 0;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void addEarnings(double amount) {
        this.totalEarnings += amount;
    }

    @Override
    public String toString() {
        return String.format("DeliveryPartner[ID=%d, Name='%s', City='%s', Status=%s, Rejections=%d/3, Earnings=Rs.%.2f]",
                getUserId(), getName(), city, status, dailyRejections, totalEarnings);
    }
}
