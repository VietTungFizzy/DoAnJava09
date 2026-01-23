package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
    Optional<UserAddress> findFirstByUserIdAndTypeAndIsDefaultTrue(Integer userId, String type);
    Optional<UserAddress> findFirstByUserIdAndTypeOrderByUpdatedAtDesc(Integer userId, String type);
    Optional<UserAddress> findByIdAndUserId(Integer id, Integer userId);
}

