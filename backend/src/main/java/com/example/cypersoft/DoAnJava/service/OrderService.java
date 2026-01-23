package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.OrderItemRequest;
import com.example.cypersoft.DoAnJava.dto.OrderRequest;
import com.example.cypersoft.DoAnJava.dto.OrderResponse;
import com.example.cypersoft.DoAnJava.dto.UpdateOrderStatusRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Integer orderId);

    List<OrderResponse> getOrdersByUserId(Integer userId);

    List<OrderResponse> getAllOrders();

    OrderResponse updateOrderStatus(Integer orderId, UpdateOrderStatusRequest request);

    void cancelOrder(Integer orderId);

    OrderResponse addItemToPendingOrder(OrderItemRequest itemRequest);
}
