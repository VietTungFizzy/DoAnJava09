package com.example.cypersoft.DoAnJava.filter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;

/**
 * Product Filter Chain - applies multiple filters in sequence
 */
@Service
public class ProductFilterChain {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private List<ProductFilter> filters;
    
    /**
     * Apply all filters and return filtered products
     */
    public Page<Product> applyFilters(String name, String brandName, String keyword,
                                    BigDecimal minPrice, BigDecimal maxPrice,
                                    String categoryIds, String sortBy, String sortDirection,
                                    String status, int page, int size) {
        
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
        
        // Create filter parameters
        ProductFilterParameters parameters = new ProductFilterParameters(
            name, brandName, keyword, minPrice, maxPrice, 
            categoryIds, sortBy, sortDirection, status, pageable
        );
        
        // Build dynamic query
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT p FROM Product p ");
        queryBuilder.append("LEFT JOIN p.brand b ");
        queryBuilder.append("LEFT JOIN p.categories c ");
        queryBuilder.append("LEFT JOIN p.skus s ");
        queryBuilder.append("WHERE p.deletedAt IS NULL ");
        queryBuilder.append("AND (s.status = 'active' OR s.status IS NULL) ");
        
        // Apply all filters
        for (ProductFilter filter : filters) {
            queryBuilder = filter.applyFilter(queryBuilder, parameters);
        }
        
        // Add status filter if provided
        if (status != null && !status.trim().isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append("p.status = :status");
        }
        
        // For now, use the existing repository method
        // TODO: Implement dynamic query execution
        return productRepository.findProductsWithFilters(
            name, keyword, brandName, minPrice, maxPrice, categoryIdList, status, pageable);
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
}
