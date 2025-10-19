package com.example.cypersoft.DoAnJava.filter;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

/**
 * Filter products by price range
 */
@Component
public class PriceFilter implements ProductFilter {
    
    @Override
    public StringBuilder applyFilter(StringBuilder queryBuilder, ProductFilterParameters parameters) {
        if (parameters.getMinPrice() != null || parameters.getMaxPrice() != null) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" AND ");
            }
            
            if (parameters.getMinPrice() != null && parameters.getMaxPrice() != null) {
                queryBuilder.append("s.price BETWEEN :minPrice AND :maxPrice");
            } else if (parameters.getMinPrice() != null) {
                queryBuilder.append("s.price >= :minPrice");
            } else if (parameters.getMaxPrice() != null) {
                queryBuilder.append("s.price <= :maxPrice");
            }
        }
        return queryBuilder;
    }
    
    @Override
    public String getFilterName() {
        return "PriceFilter";
    }
}
