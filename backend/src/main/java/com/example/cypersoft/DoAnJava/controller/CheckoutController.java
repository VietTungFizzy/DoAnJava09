package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.CheckoutRequest;
import com.example.cypersoft.DoAnJava.entity.Order;
import com.example.cypersoft.DoAnJava.repository.OrderRepository;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin
public class CheckoutController {

    @Value("${app.base-domain}")
    private String baseDomain;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create-session")
    public Map<String, String> createCheckoutSession(@RequestBody CheckoutRequest request) throws Exception {
        Order order = orderRepository.findByIdWithItems(request.getOrderId()).orElseThrow(() -> new Exception("Order not found"));

        System.out.println(order);
        // Do not touch
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(request.getSuccessUrl())
                        .setCancelUrl(request.getCancelUrl())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("VND")
                                                        .setUnitAmount(50000L)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("T-shirt")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);

        System.out.println(session.getRawJsonObject().toString());
        Map<String, String> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("url", session.getUrl());
        return response;
    }
}
