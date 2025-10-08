package com.example.cypersoft.DoAnJava.filter;

import org.springframework.stereotype.Component;

/**
 * Filter products by name
 */
@Component
public class NameFilter implements ProductFilter {
    
    @Override
    public StringBuilder applyFilter(StringBuilder queryBuilder, ProductFilterParameters parameters) {
        if (parameters.getName() != null && !parameters.getName().trim().isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append("LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))");
        }
        return queryBuilder;
    }
    
    @Override
    public String getFilterName() {
        return "NameFilter";
    }
}
