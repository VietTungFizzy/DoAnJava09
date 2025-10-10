package com.example.cypersoft.DoAnJava.dto;

import com.example.cypersoft.DoAnJava.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    private Order.OrderStatus status;
}
