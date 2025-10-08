package com.example.cypersoft.DoAnJava.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.cypersoft.DoAnJava.dto.ProductRequest;
import com.example.cypersoft.DoAnJava.dto.ProductResponse;
import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.entity.Sku;
import com.example.cypersoft.DoAnJava.filter.ProductFilterChain;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductFilterChain productFilterChain;

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

    public Page<ProductResponse> listProductsWithFilters(
            String name,
            String brandName,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String categoryIds,
            String sortBy,
            String sortDirection,
            String status,
            int page,
            int size) {
        
        // Create sort object
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Parse category IDs
        List<Integer> categoryIdList = null;
        if (categoryIds != null && !categoryIds.trim().isEmpty()) {
            categoryIdList = Arrays.stream(categoryIds.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        
        // Use the existing repository method with proper parameter mapping
        Page<Product> products = productRepository.findProductsWithFilters(
                name, keyword, brandName, minPrice, maxPrice, categoryIdList, status, pageable);
        
        // Debug: Log số lượng products
        System.out.println("Found " + products.getTotalElements() + " products");
        
        // Load SKUs for all products in this page
        List<Integer> productIds = products.getContent().stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        
        List<Sku> skus = productRepository.findActiveSkusByProductIds(productIds);
        Map<Integer, Sku> skuMap = skus.stream()
                .collect(Collectors.toMap(Sku::getProductId, sku -> sku, (existing, replacement) -> existing));
        
        // Create a map to store SKUs for each product
        final Map<Integer, Sku> finalSkuMap = skuMap;
        
        return products.map(product -> toResponseWithSku(product, finalSkuMap.get(product.getId())));
    }

    public List<ProductResponse> getAllProductsWithFilters(
            String name,
            String brandName,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String categoryIds,
            String sortBy,
            String sortDirection,
            String status) {
        
        // Create sort object
        Sort sort = createSort(sortBy, sortDirection);
        
        // Parse category IDs
        List<Integer> categoryIdList = null;
        if (categoryIds != null && !categoryIds.trim().isEmpty()) {
            categoryIdList = Arrays.stream(categoryIds.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        
        // Get all products without pagination
        List<Product> products = productRepository.findAllProductsWithFilters(
                name, keyword, brandName, minPrice, maxPrice, categoryIdList, status, sort);
        
        // Debug: Log số lượng products
        System.out.println("Found " + products.size() + " products (all)");
        
        // Load SKUs for all products
        List<Integer> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        
        List<Sku> skus = productRepository.findActiveSkusByProductIds(productIds);
        Map<Integer, Sku> skuMap = skus.stream()
                .collect(Collectors.toMap(Sku::getProductId, sku -> sku, (existing, replacement) -> existing));
        
        return products.stream()
                .map(product -> toResponseWithSku(product, skuMap.get(product.getId())))
                .collect(Collectors.toList());
    }
    
    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }
        
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        
        return Sort.by(direction, sortBy);
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
        if (request.getStoreId() != null) product.setStoreId(request.getStoreId());
        if (request.getName() != null) product.setName(request.getName());
        if (request.getSlug() != null) product.setSlug(request.getSlug());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getVisibility() != null) product.setVisibility(request.getVisibility());
        // Note: brandId would need to be handled by setting the brand relationship
        // Note: price, stock, and imageUrl are now handled in SKU and product_images tables
        // This would need to be updated to handle SKU creation/updates
    }

    private ProductResponse toResponse(Product p) {
        String brandName = null;
        Set<String> categoryNames = null;
        BigDecimal price = null;
        Integer stock = null;
        
        try {
            // Safely access brand
            if (p.getBrand() != null) {
                brandName = p.getBrand().getName();
            }
        } catch (Exception e) {
            // Brand is not loaded, set to null
            brandName = null;
        }
        
        try {
            // Safely access categories
            if (p.getCategories() != null && !p.getCategories().isEmpty()) {
                categoryNames = p.getCategories().stream()
                        .map(category -> category.getName())
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            // Categories are not loaded, set to null
            categoryNames = null;
        }
        
        try {
            // Safely access SKUs - they should be loaded now
            if (p.getSkus() != null && !p.getSkus().isEmpty()) {
                System.out.println("Product " + p.getId() + " has " + p.getSkus().size() + " SKUs");
                Sku firstActiveSku = p.getSkus().stream()
                        .filter(sku -> "active".equals(sku.getStatus()))
                        .findFirst()
                        .orElse(p.getSkus().iterator().next());
                
                if (firstActiveSku != null) {
                    price = firstActiveSku.getPrice();
                    stock = 0; // This should be populated from inventory table
                    System.out.println("Product " + p.getId() + " price: " + price);
                } else {
                    System.out.println("Product " + p.getId() + " has no active SKU");
                    price = BigDecimal.ZERO;
                    stock = 0;
                }
            } else {
                // No SKUs found
                System.out.println("Product " + p.getId() + " has no SKUs");
                price = BigDecimal.ZERO;
                stock = 0;
            }
        } catch (Exception e) {
            // SKUs are not loaded or error occurred
            System.out.println("Error accessing SKUs for product " + p.getId() + ": " + e.getMessage());
            price = BigDecimal.ZERO;
            stock = 0;
        }
        
        return new ProductResponse(
                p.getId(),
                p.getStoreId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getStatus(),
                p.getVisibility(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getDeletedAt(),
                brandName,
                categoryNames,
                price,
                stock,
                null // imageUrl - would need to be populated from product_images table
        );
    }

    private ProductResponse toResponseWithSku(Product p, Sku sku) {
        String brandName = null;
        Set<String> categoryNames = null;
        BigDecimal price = BigDecimal.ZERO;
        Integer stock = 0;
        
        try {
            // Safely access brand
            if (p.getBrand() != null) {
                brandName = p.getBrand().getName();
            }
        } catch (Exception e) {
            brandName = null;
        }
        
        try {
            // Safely access categories
            if (p.getCategories() != null && !p.getCategories().isEmpty()) {
                categoryNames = p.getCategories().stream()
                        .map(category -> category.getName())
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            categoryNames = null;
        }
        
        // Use the provided SKU
        if (sku != null) {
            price = sku.getPrice();
            stock = 0; // This should be populated from inventory table
            System.out.println("Product " + p.getId() + " price from SKU: " + price);
        } else {
            System.out.println("Product " + p.getId() + " has no SKU provided");
        }
        
        return new ProductResponse(
                p.getId(),
                p.getStoreId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getStatus(),
                p.getVisibility(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getDeletedAt(),
                brandName,
                categoryNames,
                price,
                stock,
                null // imageUrl - would need to be populated from product_images table
        );
    }
} 