package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    List<Category> findByParentIdIsNullAndIsActiveTrueOrderBySortOrder();
    
    List<Category> findByParentIdAndIsActiveTrueOrderBySortOrder(Integer parentId);
    
    Optional<Category> findByIdAndIsActiveTrue(Integer id);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.name LIKE %:keyword%")
    List<Category> findByNameContainingIgnoreCase(@Param("keyword") String keyword);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.sortOrder, c.name")
    List<Category> findAllActiveOrderBySortOrder();
}

