package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}


