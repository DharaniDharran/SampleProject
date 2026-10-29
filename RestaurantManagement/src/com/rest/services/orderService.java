package com.rest.services;

import com.rest.models.menuItem;
import com.rest.models.order;
import com.rest.models.orderItem;
import com.rest.models.table;
import com.rest.repository.orderRepository;
import java.util.List;

public class orderService {
    private final orderRepository repository;
    private final orderItemService itemService;
    private final menuItemService menuService;
    private final tableService tblService;

    public orderService(orderRepository repository, orderItemService itemService, menuItemService menuService, tableService tblService) {
        this.repository = repository;
        this.itemService = itemService;
        this.menuService = menuService;
        this.tblService = tblService;
    }

    public orderService(orderItemService itemService, menuItemService menuService, tableService tblService) {
        this(new orderRepository(), itemService, menuService, tblService);
    }

    public order createOrder(int orderId, int tableId) {
        table tbl = tblService.getTableById(tableId);
        if (tbl == null) {
            System.out.println("Error: Table ID " + tableId + " does not exist.");
            return null;
        }

        if (tbl.getStatus() == table.TableStatus.OCCUPIED) {
            System.out.println("Warning: Table ID " + tableId + " is currently OCCUPIED.");
        }

        order newOrder = new order(orderId, tableId);
        repository.save(newOrder);
        tbl.occupyTable();
        return newOrder;
    }

    public orderItem addItemToOrder(int orderItemId, int orderId, int menuItemId, int quantity) {
        order ord = repository.findById(orderId);
        if (ord == null) {
            System.out.println("Error: Order ID " + orderId + " does not exist.");
            return null;
        }

        menuItem item = menuService.getMenuItemById(menuItemId);
        if (item == null) {
            System.out.println("Error: Menu item ID " + menuItemId + " does not exist.");
            return null;
        }

        orderItem ordItem = new orderItem(orderItemId, orderId, menuItemId, quantity, item.getPrice());
        itemService.addOrderItem(ordItem);
        return ordItem;
    }

    public List<order> getAllOrders() {
        return repository.findAll();
    }

    public order getOrderById(int orderId) {
        return repository.findById(orderId);
    }

    public List<orderItem> getOrderItems(int orderId) {
        return itemService.getItemsByOrderId(orderId);
    }

    public double calculateTotal(int orderId) {
        double total = 0.0;
        for (orderItem item : itemService.getItemsByOrderId(orderId)) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public boolean updateOrderStatus(int orderId, order.OrderStatus newStatus) {
        order ord = repository.findById(orderId);
        if (ord != null) {
            ord.setStatus(newStatus);
            if (newStatus == order.OrderStatus.COMPLETED) {
                table tbl = tblService.getTableById(ord.getTable_id());
                if (tbl != null) {
                    tbl.freeTable();
                }
            }
            return true;
        }
        return false;
    }
}
