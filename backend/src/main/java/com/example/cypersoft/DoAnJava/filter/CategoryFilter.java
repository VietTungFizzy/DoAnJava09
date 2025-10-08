package com.example.cypersoft.DoAnJava.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Filter products by category IDs
 */
@Component
public class CategoryFilter implements ProductFilter {
    
    @Override
    public StringBuilder applyFilter(StringBuilder queryBuilder, ProductFilterParameters parameters) {
        if (parameters.getCategoryIds() != null && !parameters.getCategoryIds().trim().isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" AND ");
            }
            
            // Parse category IDs from comma-separated string
            List<Integer> categoryIdList = Arrays.stream(parameters.getCategoryIds().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            
            queryBuilder.append("c.id IN :categoryIds");
        }
        return queryBuilder;
    }
    
    @Override
    public String getFilterName() {
        return "CategoryFilter";
    }
}
