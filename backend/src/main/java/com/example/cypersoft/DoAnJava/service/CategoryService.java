package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.CategoryResponse;
import com.example.cypersoft.DoAnJava.entity.Category;
import com.example.cypersoft.DoAnJava.exception.CategoryNotFoundException;
import com.example.cypersoft.DoAnJava.exception.CategoryValidationException;
import com.example.cypersoft.DoAnJava.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByParentIdIsNull();
        return categories.stream()
                .map(this::convertToResponseWithSubcategories)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getSubcategories(Integer parentId) {
        List<Category> subcategories = categoryRepository.findByParentId(parentId);
        return subcategories.stream()
                .map(this::convertToResponse) // Không load subcategories của subcategories
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return convertToResponseWithSubcategories(category);
    }

    public List<CategoryResponse> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);
        return categories.stream()
                .map(this::convertToResponseWithSubcategories)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(Category category) {
        validateCategory(category);
        // Note: Category entity only has id, name, slug, parent_id, path, depth
        // Removed: createdAt, updatedAt, isActive, description, imageUrl, etc.
        Category savedCategory = categoryRepository.save(category);
        return convertToResponseWithSubcategories(savedCategory);
    }

    public CategoryResponse updateCategory(Integer id, Category categoryData) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        
        validateCategory(categoryData);
        category.setName(categoryData.getName());
        category.setSlug(categoryData.getSlug());
        category.setParentId(categoryData.getParentId());
        category.setPath(categoryData.getPath());
        category.setDepth(categoryData.getDepth());
        
        Category savedCategory = categoryRepository.save(category);
        return convertToResponseWithSubcategories(savedCategory);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        
        // Note: Category entity doesn't have isActive field, so we'll just delete it
        categoryRepository.delete(category);
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setParentId(category.getParentId());

        // Set default product count (can be enhanced later with actual product count)
        response.setProductCount(0L);

        return response;
    }
    
    private CategoryResponse convertToResponseWithSubcategories(Category category) {
        CategoryResponse response = convertToResponse(category);
        
        // Get subcategories if this is a parent category
        if (category.getParentId() == null) {
            // Note: Need to add findByParentId method to CategoryRepository
            // For now, return empty list
            response.setSubcategories(new java.util.ArrayList<>());
        }
        
        return response;
    }
    
    private void validateCategory(Category category) {
        if (!StringUtils.hasText(category.getName())) {
            throw new CategoryValidationException("Category name is required");
        }
        
        if (category.getName().length() > 255) {
            throw new CategoryValidationException("Category name must be less than 255 characters");
        }
        
        if (category.getSlug() != null && category.getSlug().length() > 255) {
            throw new CategoryValidationException("Category slug must be less than 255 characters");
        }
        
        if (category.getPath() != null && category.getPath().length() > 1000) {
            throw new CategoryValidationException("Category path must be less than 1000 characters");
        }
        
        if (category.getDepth() != null && category.getDepth() < 0) {
            throw new CategoryValidationException("Depth must be a non-negative number");
        }
    }
}

