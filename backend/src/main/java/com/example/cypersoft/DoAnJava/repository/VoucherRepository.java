package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    
    Optional<Voucher> findByCode(String code);
    
    boolean existsByCode(String code);
    
    @Query("SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND " +
           "(v.startDate IS NULL OR v.startDate <= :currentTime) AND " +
           "(v.endDate IS NULL OR v.endDate >= :currentTime)")
    List<Voucher> findActiveVouchers(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND " +
           "(v.storeId IS NULL OR v.storeId = :storeId) AND " +
           "(v.startDate IS NULL OR v.startDate <= :currentTime) AND " +
           "(v.endDate IS NULL OR v.endDate >= :currentTime)")
    List<Voucher> findActiveVouchersByStore(@Param("storeId") Integer storeId, 
                                           @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT v FROM Voucher v WHERE " +
           "(:keyword IS NULL OR v.code LIKE %:keyword% OR v.id = :keyword) AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:storeId IS NULL OR v.storeId = :storeId)")
    Page<Voucher> findVouchersWithFilters(@Param("keyword") String keyword,
                                         @Param("status") String status,
                                         @Param("storeId") Integer storeId,
                                         Pageable pageable);
    
    @Query("SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND " +
           "(v.startDate IS NULL OR v.startDate <= :currentTime) AND " +
           "(v.endDate IS NULL OR v.endDate >= :currentTime) AND " +
           "(v.storeId IS NULL OR v.storeId = :storeId) AND " +
           "(:minOrderTotal IS NULL OR v.minOrderTotal IS NULL OR v.minOrderTotal <= :minOrderTotal)")
    List<Voucher> findApplicableVouchers(@Param("currentTime") LocalDateTime currentTime,
                                        @Param("storeId") Integer storeId,
                                        @Param("minOrderTotal") java.math.BigDecimal minOrderTotal);
}
