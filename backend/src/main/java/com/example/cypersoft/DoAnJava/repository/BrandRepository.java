package com.example.cypersoft.DoAnJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cypersoft.DoAnJava.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Brand findByNameIgnoreCase(String name);
}
