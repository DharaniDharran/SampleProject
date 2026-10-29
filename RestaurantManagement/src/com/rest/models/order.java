package com.rest.models;

import java.time.LocalDateTime;

public class order {
    public enum OrderStatus {
        PLACED,
        PREPARING,
        SERVED,
        COMPLETED
    }

    private int order_id;
    private int table_id;
    private OrderStatus Status;
    private LocalDateTime order_at;

    public order(int order_id, int table_id) {
        this.order_id = order_id;
        this.table_id = table_id;
        this.Status = OrderStatus.PLACED;
        this.order_at = LocalDateTime.now();
    }

    public order(int order_id, int table_id, OrderStatus status, LocalDateTime order_at) {
        this.order_id = order_id;
        this.table_id = table_id;
        this.Status = status;
        this.order_at = order_at;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getOrderId() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setOrderId(int orderId) {
        this.order_id = orderId;
    }

    public int getTable_id() {
        return table_id;
    }

    public int getTableId() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public void setTableId(int tableId) {
        this.table_id = tableId;
    }

    public OrderStatus getStatus() {
        return Status;
    }

    public void setStatus(OrderStatus status) {
        this.Status = status;
    }

    public LocalDateTime getOrder_at() {
        return order_at;
    }

    public LocalDateTime getOrderAt() {
        return order_at;
    }

    public void setOrder_at(LocalDateTime order_at) {
        this.order_at = order_at;
    }

    public void setOrderAt(LocalDateTime orderAt) {
        this.order_at = orderAt;
    }

    public void markPreparing() {
        this.Status = OrderStatus.PREPARING;
    }

    public void markServed() {
        this.Status = OrderStatus.SERVED;
    }

    public void markCompleted() {
        this.Status = OrderStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Order ID: " + order_id + ", Table ID: " + table_id + ", Status: " + Status + ", Ordered At: " + order_at;
    }
}
