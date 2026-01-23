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
import com.example.cypersoft.DoAnJava.entity.UserAddress;
import com.example.cypersoft.DoAnJava.repository.UserAddressRepository;
import com.example.cypersoft.DoAnJava.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = getCurrentUser();

        Order order = new Order();
        order.setUser(user);

        // prefer explicit address in request, otherwise use user's default shipping address or any shipping address
        String finalAddress = null;
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            finalAddress = request.getAddress();
        } else {
            UserAddress addr = userAddressRepository
                    .findFirstByUserIdAndTypeAndIsDefaultTrue(user.getId(), "shipping")
                    .orElseGet(() -> userAddressRepository
                            .findFirstByUserIdAndTypeOrderByUpdatedAtDesc(user.getId(), "shipping")
                            .orElse(null));
            if (addr != null) {
                finalAddress = formatAddress(addr);
            }
        }

        if (finalAddress == null || finalAddress.isBlank()) {
            throw new BadRequestException("Shipping address is required");
        }

        order.setAddress(finalAddress);

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
            // populate skuId if provided; otherwise use entity default (0) for now
            if (itemReq.getSkuId() != null) {
                orderItem.setSkuId(itemReq.getSkuId());
            }
            // compute item total and set price
            BigDecimal itemTotal = itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            orderItem.setPrice(itemReq.getPrice());
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

    @Override
    public OrderResponse getPendingOrder() {
        User user = getCurrentUser();
        Optional<Order> optionalOrder = orderRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), Order.OrderStatus.pending);
        if (optionalOrder.isPresent()) {
            return convertToOrderResponse(optionalOrder.get());
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public OrderResponse addItemToPendingOrder(OrderItemRequest itemRequest) {
        User user = getCurrentUser();

        Optional<Order> optionalOrder = orderRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), Order.OrderStatus.pending);
        Order order;
        if (optionalOrder.isPresent()) {
            order = optionalOrder.get();
        } else {
            order = new Order();
            order.setUser(user);
            order.setStatus(Order.OrderStatus.pending);
            order.setTotal(BigDecimal.ZERO);
            order.setOrderItems(new ArrayList<>());
            order = orderRepository.save(order);
        }

        // Check if item already exists in the order
        Optional<OrderItem> existingItem = order.getOrderItems().stream()
                .filter(item -> item.getProductId().equals(itemRequest.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing item quantity
            OrderItem item = existingItem.get();
            int newQuantity = item.getQuantity() + itemRequest.getQuantity();
            BigDecimal oldItemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal newItemTotal = item.getPrice().multiply(BigDecimal.valueOf(newQuantity));
            item.setQuantity(newQuantity);
            order.setTotal(order.getTotal().subtract(oldItemTotal).add(newItemTotal));
        } else {
            // Create new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setStoreId(itemRequest.getStoreId());
            orderItem.setQuantity(itemRequest.getQuantity());
            if (itemRequest.getSkuId() != null) {
                orderItem.setSkuId(itemRequest.getSkuId());
            }
            orderItem.setPrice(itemRequest.getPrice());

            BigDecimal itemTotal = itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            order.getOrderItems().add(orderItem);
            order.setTotal(order.getTotal().add(itemTotal));
        }

        Order savedOrder = orderRepository.save(order);
        return convertToOrderResponse(savedOrder);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setTotal(order.getTotal());
        response.setOrderStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setFulfillmentStatus(order.getFulfillmentStatus());
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

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String formatAddress(UserAddress a) {
        StringBuilder sb = new StringBuilder();
        if (a.getFullName() != null && !a.getFullName().isBlank()) {
            sb.append(a.getFullName()).append(" - ");
        }
        if (a.getPhone() != null && !a.getPhone().isBlank()) {
            sb.append(a.getPhone()).append(" - ");
        }
        sb.append(a.getAddressLine1());
        if (a.getAddressLine2() != null && !a.getAddressLine2().isBlank()) {
            sb.append(", ").append(a.getAddressLine2());
        }
        if (a.getWard() != null && !a.getWard().isBlank()) sb.append(", ").append(a.getWard());
        if (a.getDistrict() != null && !a.getDistrict().isBlank()) sb.append(", ").append(a.getDistrict());
        if (a.getCity() != null && !a.getCity().isBlank()) sb.append(", ").append(a.getCity());
        if (a.getPostalCode() != null && !a.getPostalCode().isBlank()) sb.append(" - ").append(a.getPostalCode());
        if (a.getCountryCode() != null && !a.getCountryCode().isBlank()) sb.append(" - ").append(a.getCountryCode());
        return sb.toString();
    }
}
