package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.entity.Product;
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
public class CheckoutController {

    @Value("${app.base-domain}")
    private String baseDomain;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/create-session")
    public Map<String, String> createCheckoutSession(@RequestParam Long productId, @RequestParam Long quantity) throws Exception {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
//
//        if (quantity == null || quantity <= 0) {
//            throw new IllegalArgumentException("Quantity must be greater than 0");
//        }

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(baseDomain + "/success")
                        .setCancelUrl(baseDomain + "/cancel")
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

        Map<String, String> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("url", session.getUrl());
        return response;
    }
}
