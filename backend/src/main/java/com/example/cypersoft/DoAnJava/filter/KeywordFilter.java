package com.example.cypersoft.DoAnJava.filter;

import org.springframework.stereotype.Component;

/**
 * Filter products by keyword (searches in name and description)
 */
@Component
public class KeywordFilter implements ProductFilter {
    
    @Override
    public StringBuilder applyFilter(StringBuilder queryBuilder, ProductFilterParameters parameters) {
        if (parameters.getKeyword() != null && !parameters.getKeyword().trim().isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append("(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                              "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))");
        }
        return queryBuilder;
    }
    
    @Override
    public String getFilterName() {
        return "KeywordFilter";
    }
}
