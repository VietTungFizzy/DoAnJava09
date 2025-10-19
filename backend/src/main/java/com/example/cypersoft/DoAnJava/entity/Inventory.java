package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventories")
public class Inventory {
    @Id
    @Column(name = "sku_id")
    private Integer skuId;

    @Column(name = "quantity")
    private Integer quantity = 0;

    @Column(name = "reserved")
    private Integer reserved = 0;

    @OneToOne
    @JoinColumn(name = "sku_id", insertable = false, updatable = false)
    private Sku sku;
}
