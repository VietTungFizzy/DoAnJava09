package com.example.cypersoft.DoAnJava.filter;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.cypersoft.DoAnJava.entity.Product;

/**
 * Interface for product filtering
 */
public interface ProductFilter {
    /**
     * Apply filter to the query
     * @param queryBuilder Current query builder
     * @param parameters Filter parameters
     * @return Modified query builder
     */
    StringBuilder applyFilter(StringBuilder queryBuilder, ProductFilterParameters parameters);
    
    /**
     * Get filter name for logging
     * @return Filter name
     */
    String getFilterName();
}

/**
 * Filter parameters container
 */
class ProductFilterParameters {
    private String name;
    private String brandName;
    private String keyword;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String categoryIds;
    private String sortBy;
    private String sortDirection;
    private String status;
    private Pageable pageable;
    
    // Constructors
    public ProductFilterParameters() {}
    
    public ProductFilterParameters(String name, String brandName, String keyword, 
                                 BigDecimal minPrice, BigDecimal maxPrice, 
                                 String categoryIds, String sortBy, String sortDirection, 
                                 String status, Pageable pageable) {
        this.name = name;
        this.brandName = brandName;
        this.keyword = keyword;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.categoryIds = categoryIds;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.status = status;
        this.pageable = pageable;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    
    public String getCategoryIds() { return categoryIds; }
    public void setCategoryIds(String categoryIds) { this.categoryIds = categoryIds; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Pageable getPageable() { return pageable; }
    public void setPageable(Pageable pageable) { this.pageable = pageable; }
}
