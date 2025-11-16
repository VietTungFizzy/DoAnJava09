package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.entity.Sku;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products-with-skus")
    public String debugProductsWithSkus() {
        StringBuilder result = new StringBuilder();
        
        // Lấy 3 sản phẩm đầu tiên
        List<Product> products = productRepository.findAll().stream().limit(3).toList();
        
        for (Product p : products) {
            result.append("Product ID: ").append(p.getId()).append(", Name: ").append(p.getName()).append("\n");
            result.append("  SKUs count: ").append(p.getSkus() != null ? p.getSkus().size() : "NULL").append("\n");
            
            if (p.getSkus() != null && !p.getSkus().isEmpty()) {
                for (Sku sku : p.getSkus()) {
                    result.append("    SKU ID: ").append(sku.getId())
                          .append(", Code: ").append(sku.getSkuCode())
                          .append(", Price: ").append(sku.getPrice())
                          .append(", Status: ").append(sku.getStatus()).append("\n");
                }
            } else {
                result.append("    No SKUs found\n");
            }
            result.append("\n");
        }
        
        return result.toString();
    }
}
