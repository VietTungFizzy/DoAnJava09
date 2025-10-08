package com.example.cypersoft.DoAnJava.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.entity.Sku;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/sku-check")
    public String checkSkus() {
        StringBuilder result = new StringBuilder();
        
        // Lấy 3 sản phẩm đầu tiên
        List<Product> products = productRepository.findAll().stream().limit(3).toList();
        
        result.append("=== KIỂM TRA SKUs ===\n");
        
        for (Product p : products) {
            result.append("Product ID: ").append(p.getId()).append(", Name: ").append(p.getName()).append("\n");
            
            if (p.getSkus() != null) {
                result.append("  SKUs count: ").append(p.getSkus().size()).append("\n");
                
                if (!p.getSkus().isEmpty()) {
                    for (Sku sku : p.getSkus()) {
                        result.append("    SKU ID: ").append(sku.getId())
                              .append(", Code: ").append(sku.getSkuCode())
                              .append(", Price: ").append(sku.getPrice())
                              .append(", Status: ").append(sku.getStatus()).append("\n");
                    }
                } else {
                    result.append("    No SKUs in collection\n");
                }
            } else {
                result.append("  SKUs is NULL\n");
            }
            result.append("\n");
        }
        
        return result.toString();
    }
}