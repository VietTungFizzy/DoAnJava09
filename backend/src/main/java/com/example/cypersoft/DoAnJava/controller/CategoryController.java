package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.CategoryResponse;
import com.example.cypersoft.DoAnJava.entity.Category;
import com.example.cypersoft.DoAnJava.exception.CategoryNotFoundException;
import com.example.cypersoft.DoAnJava.exception.CategoryValidationException;
import com.example.cypersoft.DoAnJava.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer id) {
        try {
            CategoryResponse category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<List<CategoryResponse>> getSubcategories(@PathVariable Integer id) {
        List<CategoryResponse> subcategories = categoryService.getSubcategories(id);
        return ResponseEntity.ok(subcategories);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> searchCategories(@RequestParam String keyword) {
        List<CategoryResponse> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody Category category) {
        try {
            CategoryResponse createdCategory = categoryService.createCategory(category);
            return ResponseEntity.ok(createdCategory);
        } catch (CategoryValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        try {
            CategoryResponse updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CategoryValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
