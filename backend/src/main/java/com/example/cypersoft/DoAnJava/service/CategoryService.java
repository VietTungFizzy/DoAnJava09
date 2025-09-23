package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.CategoryResponse;
import com.example.cypersoft.DoAnJava.entity.Category;
import com.example.cypersoft.DoAnJava.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByParentIdIsNullAndIsActiveTrueOrderBySortOrder();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getSubcategories(Integer parentId) {
        List<Category> subcategories = categoryRepository.findByParentIdAndIsActiveTrueOrderBySortOrder(parentId);
        return subcategories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return convertToResponse(category);
    }

    public List<CategoryResponse> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setIsActive(true);
        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }

    public CategoryResponse updateCategory(Integer id, Category categoryData) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setName(categoryData.getName());
        category.setDescription(categoryData.getDescription());
        category.setImageUrl(categoryData.getImageUrl());
        category.setIconUrl(categoryData.getIconUrl());
        category.setBannerUrl(categoryData.getBannerUrl());
        category.setParentId(categoryData.getParentId());
        category.setSortOrder(categoryData.getSortOrder());
        category.setUpdatedAt(LocalDateTime.now());
        
        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setIsActive(false);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setParentId(category.getParentId());
        response.setSortOrder(category.getSortOrder());
        response.setIsActive(category.getIsActive());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        // Set default product count
        response.setProductCount(0L);

        // Get subcategories if this is a parent category
        if (category.getParentId() == null) {
            List<CategoryResponse> subcategories = getSubcategories(category.getId());
            response.setSubcategories(subcategories);
        }

        return response;
    }
}

