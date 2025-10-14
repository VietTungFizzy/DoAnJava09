package com.example.cypersoft.DoAnJava.dto;

import com.example.cypersoft.DoAnJava.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Integer id;
    private Integer userId;
    private BigDecimal total;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private String address;
    private Integer voucherId;
    private List<OrderItemResponse> orderItems;
}
