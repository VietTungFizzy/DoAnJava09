package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.CheckoutRequest;
import com.example.cypersoft.DoAnJava.entity.Order;
import com.example.cypersoft.DoAnJava.entity.OrderItem;
import com.example.cypersoft.DoAnJava.entity.Product;
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

        // Build SessionCreateParams with line items from order
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(request.getSuccessUrl())
                .setCancelUrl(request.getCancelUrl());

        // Add shipping address collection
        if (order.getAddress() != null && !order.getAddress().isBlank()) {
            paramsBuilder.setShippingAddressCollection(
                    SessionCreateParams.ShippingAddressCollection.builder()
                            .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.VN)
                            .build()
            );
        }

        // Add line items from order items
        for (OrderItem orderItem : order.getOrderItems()) {
            // Fetch Product to get product details
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new Exception("Product not found for order item: " + orderItem.getId()));

            // Get product name
            String productName = product.getName();

            // Convert price from BigDecimal to Long (VND doesn't have decimal places)
            Long unitAmount = orderItem.getPrice().longValue();

            // Build line item
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(orderItem.getQuantity().longValue())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("VND")
                                    .setUnitAmount(unitAmount)
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(productName)
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            paramsBuilder.addLineItem(lineItem);
        }

        SessionCreateParams params = paramsBuilder.build();

        Session session = Session.create(params);

        System.out.println(session.getRawJsonObject().toString());
        Map<String, String> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("url", session.getUrl());
        return response;
    }
}
