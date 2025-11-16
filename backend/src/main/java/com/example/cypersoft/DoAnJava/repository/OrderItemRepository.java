package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // Tìm tất cả order items theo orderId
    List<OrderItem> findByOrderId(Integer orderId);

    // Tìm order items theo productId
    List<OrderItem> findByProductId(Integer productId);

    // Tìm order items theo storeId
    List<OrderItem> findByStoreId(Integer storeId);
}
