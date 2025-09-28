package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VoucherType type;

    @Column(name = "value", precision = 12, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "max_discount", precision = 12, scale = 2)
    private BigDecimal maxDiscount;

    @Column(name = "min_order_total", precision = 12, scale = 2)
    private BigDecimal minOrderTotal;

    @Column(name = "store_id")
    private Integer storeId; // NULL = toàn sàn

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VoucherStatus status = VoucherStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum VoucherType {
        FIXED, PERCENT
    }

    public enum VoucherStatus {
        ACTIVE, INACTIVE, EXPIRED
    }
}
