package com.example.cypersoft.DoAnJava.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.cypersoft.DoAnJava.dto.ProductRequest;
import com.example.cypersoft.DoAnJava.dto.ProductResponse;
import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<ProductResponse> listProducts(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (keyword != null && !keyword.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        } else if (status != null && !status.isBlank()) {
            products = productRepository.findByStatus(status.trim(), pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(this::toResponse);
    }

    public ProductResponse getById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse update(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        applyRequest(product, request);
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public void delete(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private void applyRequest(Product product, ProductRequest request) {
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getImageUrl(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
} 