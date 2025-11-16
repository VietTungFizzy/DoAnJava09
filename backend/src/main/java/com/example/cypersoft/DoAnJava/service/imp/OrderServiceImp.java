package com.example.cypersoft.DoAnJava.service.imp;

import com.example.cypersoft.DoAnJava.dto.*;
import com.example.cypersoft.DoAnJava.entity.Order;
import com.example.cypersoft.DoAnJava.entity.OrderItem;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.entity.Voucher;
import com.example.cypersoft.DoAnJava.repository.OrderItemRepository;
import com.example.cypersoft.DoAnJava.repository.OrderRepository;
import com.example.cypersoft.DoAnJava.repository.UserRepository;
import com.example.cypersoft.DoAnJava.repository.VoucherRepository;
import com.example.cypersoft.DoAnJava.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(request.getAddress());

        if (request.getVoucherId() != null) {
            Voucher voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + request.getVoucherId()));
            order.setVoucher(voucher);
        }

        // Tính tổng tiền từ các order items
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(itemReq.getProductId());
            orderItem.setStoreId(itemReq.getStoreId());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(itemReq.getPrice());

            BigDecimal itemTotal = itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(itemTotal);

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotal(total);
        order.setStatus(Order.OrderStatus.pending);

        Order savedOrder = orderRepository.save(order);
        return convertToOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return convertToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderRepository.findOrdersByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAllOrderByCreatedAtDesc();
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToOrderResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() == Order.OrderStatus.delivered ||
            order.getStatus() == Order.OrderStatus.cancelled) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.cancelled);
        orderRepository.save(order);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setAddress(order.getAddress());
        response.setVoucherId(order.getVoucher() != null ? order.getVoucher().getId() : null);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(this::convertToOrderItemResponse)
                .collect(Collectors.toList());
        response.setOrderItems(itemResponses);

        return response;
    }

    private OrderItemResponse convertToOrderItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setStoreId(item.getStoreId());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        return response;
    }
}
