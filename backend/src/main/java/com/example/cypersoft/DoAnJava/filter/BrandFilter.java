package com.example.cypersoft.DoAnJava.filter;

import org.springframework.stereotype.Component;

/**
 * Filter products by brand name
 */
@Component
public class BrandFilter implements ProductFilter {
    
    @Override
    public StringBuilder applyFilter(StringBuilder queryBuilder, ProductFilterParameters parameters) {
        if (parameters.getBrandName() != null && !parameters.getBrandName().trim().isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append("LOWER(b.name) LIKE LOWER(CONCAT('%', :brandName, '%'))");
        }
        return queryBuilder;
    }
    
    @Override
    public String getFilterName() {
        return "BrandFilter";
    }
}
