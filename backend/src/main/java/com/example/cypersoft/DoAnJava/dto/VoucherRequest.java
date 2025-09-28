package com.example.cypersoft.DoAnJava.dto;

import com.example.cypersoft.DoAnJava.entity.Voucher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequest {
    private String code;
    private Voucher.VoucherType type;
    private BigDecimal value;
    private BigDecimal maxDiscount;
    private BigDecimal minOrderTotal;
    private Integer storeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usageLimitPerUser;
    private Voucher.VoucherStatus status;
}
