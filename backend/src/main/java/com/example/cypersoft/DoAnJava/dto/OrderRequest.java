package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Integer userId;
    private Integer voucherId;
    private String address;
    private List<OrderItemRequest> orderItems;
}
