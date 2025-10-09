package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.VoucherRequest;
import com.example.cypersoft.DoAnJava.dto.VoucherResponse;
import com.example.cypersoft.DoAnJava.entity.Voucher;
import com.example.cypersoft.DoAnJava.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {
    
    private final VoucherRepository voucherRepository;
    
    public Page<VoucherResponse> listVouchers(String keyword, String status, Integer storeId, 
                                            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Voucher> vouchers = voucherRepository.findVouchersWithFilters(
            keyword, status, storeId, pageable);
        
        return vouchers.map(this::convertToResponse);
    }
    
    public VoucherResponse getById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
        return convertToResponse(voucher);
    }
    
    public VoucherResponse getByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Voucher not found with code: " + code));
        return convertToResponse(voucher);
    }
    
    @Transactional
    public VoucherResponse create(VoucherRequest request) {
        // Validate code uniqueness
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Voucher code already exists: " + request.getCode());
        }
        
        // Validate voucher data
        validateVoucherRequest(request);
        
        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setType(request.getType());
        voucher.setValue(request.getValue());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setMinOrderTotal(request.getMinOrderTotal());
        voucher.setStoreId(request.getStoreId());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setUsageLimitPerUser(request.getUsageLimitPerUser());
        voucher.setStatus(request.getStatus() != null ? request.getStatus() : Voucher.VoucherStatus.ACTIVE);
        
        Voucher savedVoucher = voucherRepository.save(voucher);
        return convertToResponse(savedVoucher);
    }
    
    @Transactional
    public VoucherResponse update(Integer id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
        
        // Validate code uniqueness (if code is being changed)
        if (!voucher.getCode().equals(request.getCode()) && 
            voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Voucher code already exists: " + request.getCode());
        }
        
        // Validate voucher data
        validateVoucherRequest(request);
        
        voucher.setCode(request.getCode());
        voucher.setType(request.getType());
        voucher.setValue(request.getValue());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setMinOrderTotal(request.getMinOrderTotal());
        voucher.setStoreId(request.getStoreId());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setUsageLimitPerUser(request.getUsageLimitPerUser());
        voucher.setStatus(request.getStatus());
        
        Voucher savedVoucher = voucherRepository.save(voucher);
        return convertToResponse(savedVoucher);
    }
    
    @Transactional
    public void delete(Integer id) {
        if (!voucherRepository.existsById(id)) {
            throw new RuntimeException("Voucher not found with id: " + id);
        }
        voucherRepository.deleteById(id);
    }
    
    public List<VoucherResponse> getActiveVouchers() {
        List<Voucher> vouchers = voucherRepository.findActiveVouchers(LocalDateTime.now());
        return vouchers.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public List<VoucherResponse> getActiveVouchersByStore(Integer storeId) {
        List<Voucher> vouchers = voucherRepository.findActiveVouchersByStore(storeId, LocalDateTime.now());
        return vouchers.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public List<VoucherResponse> getApplicableVouchers(Integer storeId, BigDecimal orderTotal) {
        List<Voucher> vouchers = voucherRepository.findApplicableVouchers(
            LocalDateTime.now(), storeId, orderTotal);
        return vouchers.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public BigDecimal calculateDiscount(String voucherCode, BigDecimal orderTotal, Integer storeId) {
        Voucher voucher = voucherRepository.findByCode(voucherCode)
            .orElseThrow(() -> new RuntimeException("Voucher not found: " + voucherCode));
        
        // Check if voucher is applicable
        if (!isVoucherApplicable(voucher, orderTotal, storeId)) {
            throw new RuntimeException("Voucher is not applicable for this order");
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        if (voucher.getType() == Voucher.VoucherType.FIXED) {
            discount = voucher.getValue();
        } else if (voucher.getType() == Voucher.VoucherType.PERCENT) {
            discount = orderTotal.multiply(voucher.getValue()).divide(BigDecimal.valueOf(100));
        }
        
        // Apply max discount limit if set
        if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
            discount = voucher.getMaxDiscount();
        }
        
        // Ensure discount doesn't exceed order total
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }
        
        return discount;
    }
    
    private boolean isVoucherApplicable(Voucher voucher, BigDecimal orderTotal, Integer storeId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check status
        if (voucher.getStatus() != Voucher.VoucherStatus.ACTIVE) {
            return false;
        }
        
        // Check date range
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            return false;
        }
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            return false;
        }
        
        // Check store restriction
        if (voucher.getStoreId() != null && !voucher.getStoreId().equals(storeId)) {
            return false;
        }
        
        // Check minimum order total
        if (voucher.getMinOrderTotal() != null && orderTotal.compareTo(voucher.getMinOrderTotal()) < 0) {
            return false;
        }
        
        return true;
    }
    
    private void validateVoucherRequest(VoucherRequest request) {
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new RuntimeException("Voucher code is required");
        }
        
        if (request.getType() == null) {
            throw new RuntimeException("Voucher type is required");
        }
        
        if (request.getValue() == null || request.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Voucher value must be greater than 0");
        }
        
        if (request.getType() == Voucher.VoucherType.PERCENT && 
            request.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Percentage voucher value cannot exceed 100%");
        }
        
        if (request.getStartDate() != null && request.getEndDate() != null && 
            request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }
        
        if (request.getUsageLimit() != null && request.getUsageLimit() <= 0) {
            throw new RuntimeException("Usage limit must be greater than 0");
        }
        
        if (request.getUsageLimitPerUser() != null && request.getUsageLimitPerUser() <= 0) {
            throw new RuntimeException("Usage limit per user must be greater than 0");
        }
    }
    
    private VoucherResponse convertToResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setCode(voucher.getCode());
        response.setType(voucher.getType());
        response.setValue(voucher.getValue());
        response.setMaxDiscount(voucher.getMaxDiscount());
        response.setMinOrderTotal(voucher.getMinOrderTotal());
        response.setStoreId(voucher.getStoreId());
        response.setStartDate(voucher.getStartDate());
        response.setEndDate(voucher.getEndDate());
        response.setUsageLimit(voucher.getUsageLimit());
        response.setUsageLimitPerUser(voucher.getUsageLimitPerUser());
        response.setStatus(voucher.getStatus());
        response.setCreatedAt(voucher.getCreatedAt());
        
        // Compute additional fields
        LocalDateTime now = LocalDateTime.now();
        response.setIsExpired(voucher.getEndDate() != null && now.isAfter(voucher.getEndDate()));
        response.setIsActive(voucher.getStatus() == Voucher.VoucherStatus.ACTIVE && 
                           (voucher.getStartDate() == null || now.isAfter(voucher.getStartDate())) &&
                           (voucher.getEndDate() == null || now.isBefore(voucher.getEndDate())));
        
        // Generate description
        StringBuilder description = new StringBuilder();
        if (voucher.getType() == Voucher.VoucherType.FIXED) {
            description.append("Giảm ").append(voucher.getValue()).append(" VND");
        } else {
            description.append("Giảm ").append(voucher.getValue()).append("%");
        }
        
        if (voucher.getMinOrderTotal() != null) {
            description.append(" cho đơn hàng từ ").append(voucher.getMinOrderTotal()).append(" VND");
        }
        
        if (voucher.getMaxDiscount() != null) {
            description.append(" (tối đa ").append(voucher.getMaxDiscount()).append(" VND)");
        }
        
        response.setDescription(description.toString());
        
        return response;
    }
}
