package com.example.cypersoft.DoAnJava.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductFilterChain productFilterChain;

    @Autowired
    private WishlistService wishlistService; // used to check if SKU is in current user's wishlist

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

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
        log.info("Found {} products", products.getTotalElements());
        
        // Load SKUs for all products in this page
        List<Integer> productIds = products.getContent().stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        System.out.println(productIds);
        List<Sku> skus = productRepository.findActiveSkusByProductIds(productIds);
        Map<Integer, Sku> skuMap = skus.stream()
                .collect(Collectors.toMap(Sku::getProductId, sku -> sku, (existing, replacement) -> existing));
        
        // Create a map to store SKUs for each product
        final Map<Integer, Sku> finalSkuMap = skuMap;
        
        // Determine if user is authenticated
        boolean loggedIn = isUserLoggedIn();

        return products.map(product -> {
            Sku sku = finalSkuMap.get(product.getId());
            Integer skuId = sku != null ? sku.getId() : null;
            boolean inWishlist = false;
            if (loggedIn && skuId != null) {
                try {
                    inWishlist = wishlistService.isProductInWishlist(skuId);
                } catch (Exception e) {
                    // If wishlist check fails for any reason (e.g. user not found), default to false
                    log.debug("Failed to check wishlist for sku {}: {}", skuId, e.toString());
                    inWishlist = false;
                }
            }
            return toResponseWithSku(product, sku, inWishlist);
        });
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
        log.info("Found {} products (all)", products.size());
        
        // Load SKUs for all products
        List<Integer> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        
        List<Sku> skus = productRepository.findActiveSkusByProductIds(productIds);
        Map<Integer, Sku> skuMap = skus.stream()
                .collect(Collectors.toMap(Sku::getProductId, sku -> sku, (existing, replacement) -> existing));
        
        // Determine if user is authenticated
        boolean loggedIn = isUserLoggedIn();

        return products.stream()
                .map(product -> {
                    Sku sku = skuMap.get(product.getId());
                    Integer skuId = sku != null ? sku.getId() : null;
                    boolean inWishlist = false;
                    if (loggedIn && skuId != null) {
                        try {
                            inWishlist = wishlistService.isProductInWishlist(skuId);
                        } catch (Exception e) {
                            log.debug("Failed to check wishlist for sku {}: {}", skuId, e.toString());
                            inWishlist = false;
                        }
                    }
                    return toResponseWithSku(product, sku, inWishlist);
                })
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
        Sku firstActiveSku = null; // moved out so we can use it for wishlist check and image

        try {
            // Safely access brand
            if (p.getBrand() != null) {
                brandName = p.getBrand().getName();
            }
        } catch (LazyInitializationException | EntityNotFoundException e) {
            // Brand is not loaded, set to null
            log.warn("Brand not initialized for product {}: {}", p.getId(), e.toString());
            brandName = null;
        } catch (RuntimeException e) {
            log.error("Unexpected error accessing brand for product {}", p.getId(), e);
            brandName = null;
        }
        
        try {
            // Safely access categories
            if (p.getCategories() != null && !p.getCategories().isEmpty()) {
                categoryNames = p.getCategories().stream()
                        .map(category -> category.getName())
                        .collect(Collectors.toSet());
            }
        } catch (LazyInitializationException | EntityNotFoundException e) {
            // Categories are not loaded, set to null
            log.warn("Categories not initialized for product {}: {}", p.getId(), e.toString());
            categoryNames = null;
        } catch (RuntimeException e) {
            log.error("Unexpected error accessing categories for product {}", p.getId(), e);
            categoryNames = null;
        }
        
        try {
            // Safely access SKUs - they should be loaded now
            if (p.getSkus() != null && !p.getSkus().isEmpty()) {
                log.debug("Product {} has {} SKUs", p.getId(), p.getSkus().size());
                firstActiveSku = p.getSkus().stream()
                        .filter(sku -> "active".equals(sku.getStatus()))
                        .findFirst()
                        .orElse(p.getSkus().iterator().next());
                
                if (firstActiveSku != null) {
                    price = firstActiveSku.getPrice();
                    stock = 0; // This should be populated from inventory table
                    log.debug("Product {} price: {}", p.getId(), price);
                } else {
                    log.debug("Product {} has no active SKU", p.getId());
                    price = BigDecimal.ZERO;
                    stock = 0;
                }
            } else {
                // No SKUs found
                log.debug("Product {} has no SKUs", p.getId());
                price = BigDecimal.ZERO;
                stock = 0;
            }
        } catch (LazyInitializationException | EntityNotFoundException e) {
            // SKUs are not loaded or entity not found
            log.warn("Accessing SKUs failed for product {}: {}", p.getId(), e.toString());
            price = BigDecimal.ZERO;
            stock = 0;
        } catch (RuntimeException e) {
            log.error("Unexpected error accessing SKUs for product {}", p.getId(), e);
            price = BigDecimal.ZERO;
            stock = 0;
        }

        // Determine if user is authenticated and check wishlist for the SKU if available
        boolean loggedIn = isUserLoggedIn();
        boolean isInWishlist = false;
        if (loggedIn && firstActiveSku != null) {
            try {
                isInWishlist = wishlistService.isProductInWishlist(firstActiveSku.getId());
            } catch (Exception e) {
                log.debug("Failed to check wishlist for sku {}: {}", firstActiveSku.getId(), e.toString());
                isInWishlist = false;
            }
        }

        // Use product imageUrl if available
        String imageUrl = null;
        List<String> images = null;
        try {
            imageUrl = p.getImageUrl();
        } catch (RuntimeException e) {
            log.debug("Failed to access imageUrl for product {}: {}", p.getId(), e.toString());
            imageUrl = null;
        }

        try {
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                images = p.getImages().stream()
                        .map(img -> img.getImageUrl())
                        .collect(Collectors.toList());
            }
        } catch (LazyInitializationException | EntityNotFoundException e) {
            log.debug("Failed to access images for product {}: {}", p.getId(), e.toString());
            images = null;
        } catch (RuntimeException e) {
            log.error("Unexpected error accessing images for product {}", p.getId(), e);
            images = null;
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
                imageUrl, // single image for backward compatibility
                images, // renamed to 'images' to match frontend
                isInWishlist // isInWishlist populated
        );
    }

    private ProductResponse toResponseWithSku(Product p, Sku sku, boolean isInWishlist) {
        String brandName = null;
        Set<String> categoryNames = null;
        BigDecimal price = BigDecimal.ZERO;
        Integer stock = 0;
        List<String> images = null;

        try {
            // Safely access brand
            if (p.getBrand() != null) {
                brandName = p.getBrand().getName();
            }
        } catch (LazyInitializationException | EntityNotFoundException e) {
            log.warn("Brand not initialized for product {}: {}", p.getId(), e.toString());
            brandName = null;
        } catch (RuntimeException e) {
            log.error("Unexpected error accessing brand for product {}: {}", p.getId(), e);
            brandName = null;
        }
        
        try {
            // Safely access categories
            if (p.getCategories() != null && !p.getCategories().isEmpty()) {
                categoryNames = p.getCategories().stream()
                        .map(category -> category.getName())
                        .collect(Collectors.toSet());
            }
        } catch (LazyInitializationException | EntityNotFoundException e) {
            log.warn("Categories not initialized for product {}: {}", p.getId(), e.toString());
            categoryNames = null;
        } catch (RuntimeException e) {
            categoryNames = null;
            log.error("Unexpected error accessing categories for product {}: {}", p.getId(), e);
        }
        
        // Use the provided SKU
        if (sku != null) {
            price = sku.getPrice();
            stock = 0; // This should be populated from inventory table
            log.debug("Product {} price from SKU: {}", p.getId(), price);
        } else {
            log.debug("Product {} has no SKU provided", p.getId());
        }

        try {
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                images = p.getImages().stream()
                        .map(img -> img.getImageUrl())
                        .collect(Collectors.toList());
            }
        } catch (LazyInitializationException | EntityNotFoundException e) {
            log.debug("Failed to access images for product {}: {}", p.getId(), e.toString());
            images = null;
        } catch (RuntimeException e) {
            log.error("Unexpected error accessing images for product {}", p.getId(), e);
            images = null;
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
                p.getImageUrl(), // imageUrl - would need to be populated from product_images table
                images,
                isInWishlist
        );
    }
    // Helper to centralize authentication check (prevents duplicate code)
    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()));
    }

    // Add method to get random related products (same category)
    public List<ProductResponse> getRandomProductsInSameCategory(Integer productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));

        // Get category IDs
        List<Integer> categoryIdList = null;
        try {
            if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                categoryIdList = product.getCategories().stream()
                        .map(cat -> cat.getId())
                        .collect(Collectors.toList());
            }
        } catch (RuntimeException e) {
            log.debug("Failed to access categories for product {}: {}", productId, e.toString());
            categoryIdList = null;
        }

        if (categoryIdList == null || categoryIdList.isEmpty()) {
            return Collections.emptyList();
        }

        // Load candidate products that belong to these categories (limit fetch size to a reasonably large number)
        List<Product> candidates = productRepository.findByCategoryIds(categoryIdList, PageRequest.of(0, 1000))
                .getContent().stream()
                .filter(p -> !p.getId().equals(productId))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) return Collections.emptyList();

        // Shuffle and pick up to 'limit' products
        Collections.shuffle(candidates);
        int take = Math.min(limit, candidates.size());
        List<Product> selected = candidates.subList(0, take);

        // Load SKUs for selected products to populate price and wishlist checks efficiently
        List<Integer> selectedIds = selected.stream().map(Product::getId).collect(Collectors.toList());
        List<Sku> skus = productRepository.findActiveSkusByProductIds(selectedIds);
        Map<Integer, Sku> skuMap = skus.stream()
                .collect(Collectors.toMap(Sku::getProductId, sku -> sku, (existing, replacement) -> existing));

        boolean loggedIn = isUserLoggedIn();

        return selected.stream().map(p -> {
            Sku sku = skuMap.get(p.getId());
            Integer skuId = sku != null ? sku.getId() : null;
            boolean inWishlist = false;
            if (loggedIn && skuId != null) {
                try {
                    inWishlist = wishlistService.isProductInWishlist(skuId);
                } catch (Exception e) {
                    log.debug("Failed to check wishlist for sku {}: {}", skuId, e.toString());
                    inWishlist = false;
                }
            }
            return toResponseWithSku(p, sku, inWishlist);
        }).collect(Collectors.toList());
    }
}
