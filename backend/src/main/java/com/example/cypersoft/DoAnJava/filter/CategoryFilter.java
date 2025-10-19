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

            // Validate and parse category IDs from comma-separated string
            List<Integer> categoryIdList = Arrays.stream(parameters.getCategoryIds().split(","))
                    .map(String::trim)
                    .filter(token -> !token.isEmpty())
                    .peek(token -> {
                        if (!token.matches("\\d+")) {
                            throw new IllegalArgumentException("Invalid categoryIds: must be comma-separated integers");
                        }
                    })
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (categoryIdList.isEmpty()) {
                return queryBuilder; // nothing to filter by after validation
            }

            queryBuilder.append("c.id IN :categoryIds");
        }
        return queryBuilder;
    }
    
    @Override
    public String getFilterName() {
        return "CategoryFilter";
    }
}
