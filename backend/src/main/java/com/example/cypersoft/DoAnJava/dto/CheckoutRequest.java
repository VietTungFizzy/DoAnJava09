package com.example.cypersoft.DoAnJava.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private int orderId;
    private String successUrl;
    private String cancelUrl;
}
