package com.example.cypersoft.DoAnJava.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private int productId;
    private int quantity;
    private String successUrl;
    private String cancelUrl;
}

